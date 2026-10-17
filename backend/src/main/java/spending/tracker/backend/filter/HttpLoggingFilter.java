package spending.tracker.backend.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import spending.tracker.backend.logging.LogSanitizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * Фильтр логирования HTTP: собирает данные запроса/ответа, раскладывает их по
 * структурированным полям (MDC) и логирует тела запроса/ответа на уровне INFO
 * с маскированием sensitive-данных. Тела также включаются в сообщение, чтобы
 * быть видимыми в консоли, а не только в Graylog.
 */
@Component
public class HttpLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger("HTTP");

    static final String CORRELATION_ID = "correlation_id";

    // Структурированные поля HTTP-лога (становятся отдельными полями JSON/GELF)
    static final String HTTP_METHOD = "http.method";
    static final String HTTP_URI = "http.uri";
    static final String HTTP_QUERY = "http.query";
    static final String HTTP_STATUS = "http.status";
    static final String HTTP_DURATION_MS = "http.duration_ms";
    static final String HTTP_REQUEST_BODY = "http.request_body";
    static final String HTTP_RESPONSE_BODY = "http.response_body";
    static final String HTTP_USER_AGENT = "http.user_agent";
    static final String HTTP_CONTENT_TYPE = "http.content_type";
    static final String HTTP_X_REQUESTED_WITH = "http.x_requested_with";

    static final int CACHE_LIMIT = 10000;
    static final int MAX_BODY_LENGTH = 500;
    static final String TRUNCATED_SUFFIX = "... [truncated]";
    static final String BODY_NOT_LOGGED = "<not logged>";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, correlationId);

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("X-Correlation-Id", correlationId);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest, CACHE_LIMIT);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // Логируем после обработки, до сброса MDC и отдачи тела клиенту
            logRequestResponse(wrappedRequest, wrappedResponse, startTime);
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request,
                                    ContentCachingResponseWrapper response,
                                    long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        MDC.put(HTTP_METHOD, nullToEmpty(request.getMethod()));
        MDC.put(HTTP_URI, nullToEmpty(request.getRequestURI()));
        MDC.put(HTTP_QUERY, nullToEmpty(request.getQueryString()));
        MDC.put(HTTP_STATUS, String.valueOf(response.getStatus()));
        MDC.put(HTTP_DURATION_MS, String.valueOf(duration));
        MDC.put(HTTP_USER_AGENT, nullToEmpty(request.getHeader("user-agent")));
        MDC.put(HTTP_CONTENT_TYPE, nullToEmpty(request.getContentType()));
        MDC.put(HTTP_X_REQUESTED_WITH, nullToEmpty(request.getHeader("x-requested-with")));

        // Тела запроса/ответа логируются на INFO с маскированием sensitive-данных.
        String requestBody = resolveRequestBody(request);
        String responseBody = resolveResponseBody(response);
        MDC.put(HTTP_REQUEST_BODY, requestBody);
        MDC.put(HTTP_RESPONSE_BODY, responseBody);

        // Тела дублируются в сообщении, чтобы их было видно и в консоли (не только в Graylog).
        logger.info("{} {} -> {} | request_body={} | response_body={}",
                request.getMethod(), request.getRequestURI(), response.getStatus(), requestBody, responseBody);
    }

    private String resolveRequestBody(ContentCachingRequestWrapper request) {
        if (isBodyLoggingUnsupported(request.getContentType())) {
            return BODY_NOT_LOGGED;
        }
        return truncateBody(LogSanitizer.mask(new String(request.getContentAsByteArray(), StandardCharsets.UTF_8)));
    }

    private String resolveResponseBody(ContentCachingResponseWrapper response) {
        if (isBodyLoggingUnsupported(response.getContentType())) {
            return BODY_NOT_LOGGED;
        }
        return truncateBody(LogSanitizer.mask(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8)));
    }

    /**
     * Тела multipart и бинарных типов не логируются: мусор в логах и риск OOM
     * на больших телах (R12).
     */
    private boolean isBodyLoggingUnsupported(String contentType) {
        if (contentType == null) {
            return false;
        }
        String lower = contentType.toLowerCase(Locale.ROOT);
        if (lower.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return true;
        }
        return lower.startsWith("image/")
                || lower.startsWith("audio/")
                || lower.startsWith("video/")
                || lower.contains("application/octet-stream")
                || lower.contains("application/pdf")
                || lower.contains("application/zip")
                || lower.contains("application/gzip")
                || lower.contains("application/x-protobuf");
    }

    private String truncateBody(String body) {
        if (body == null || body.isEmpty()) {
            return "";
        }
        if (body.length() > MAX_BODY_LENGTH) {
            return body.substring(0, MAX_BODY_LENGTH) + TRUNCATED_SUFFIX;
        }
        return body;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
