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
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class HttpLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger("HTTP");
    private static final String CORRELATION_ID = "correlation_id";
    private static final int CACHE_LIMIT = 10000;

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

            // Логируем только после обработки
            logRequestResponse(wrappedRequest, wrappedResponse, startTime);

        } finally {
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request,
                                    ContentCachingResponseWrapper response,
                                    long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        // Получаем тело запроса
        byte[] requestBody = request.getContentAsByteArray();
        String reqBody = new String(requestBody, StandardCharsets.UTF_8);

        // Получаем тело ответа
        byte[] responseBody = response.getContentAsByteArray();
        String respBody = new String(responseBody, StandardCharsets.UTF_8);

        // Ограничиваем размер для логирования
        reqBody = truncateBody(reqBody);
        respBody = truncateBody(respBody);

        // Структурированное логирование в одну строку для Graylog
        logger.info(String.format(
                "http_request|method=%s|uri=%s|query=%s|status=%d|duration=%d|headers=%s|request_body=%s|response_body=%s",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString() != null ? request.getQueryString() : "",
                response.getStatus(),
                duration,
                getImportantHeaders(request),
                reqBody,
                respBody
        ));
    }

    private String truncateBody(String body) {
        if (body == null || body.isEmpty()) return "";
        if (body.length() > 500) {
            return body.substring(0, 500) + "... [truncated]";
        }
        return body;
    }

    private String getImportantHeaders(HttpServletRequest request) {
        // Логируем только важные заголовки
        List<String> importantHeaders = Arrays.asList(
                "user-agent", "content-type", "authorization", "x-requested-with"
        );

        StringBuilder headers = new StringBuilder();
        for (String headerName : importantHeaders) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isEmpty()) {
                if (headers.length() > 0) headers.append("; ");
                headers.append(headerName).append("=");

                // Скрываем sensitive данные
                if (headerName.equals("authorization")) {
                    headers.append("***");
                } else if (headerName.equals("cookie")) {
                    headers.append(headerValue.length() > 50 ? "***" : headerValue);
                } else {
                    headers.append(headerValue.length() > 100 ? headerValue.substring(0, 100) : headerValue);
                }
            }
        }
        return headers.toString();
    }
}