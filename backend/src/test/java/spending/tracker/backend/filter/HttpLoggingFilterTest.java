package spending.tracker.backend.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpLoggingFilterTest {

    private final HttpLoggingFilter filter = new HttpLoggingFilter();

    private Logger httpLogger;
    private Level originalLevel;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        httpLogger = (Logger) LoggerFactory.getLogger("HTTP");
        originalLevel = httpLogger.getLevel();
        appender = new ListAppender<>();
        appender.start();
        httpLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        httpLogger.detachAppender(appender);
        appender.stop();
        httpLogger.setLevel(originalLevel);
        MDC.clear();
    }

    @Test
    void bodiesAreLoggedAndMaskedAtInfo() throws Exception {
        httpLogger.setLevel(Level.INFO);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/user");
        request.setContentType("application/json");
        request.setContent("{\"email\":\"user@example.com\",\"password\":\"secret123\"}"
                .getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();

        runFilter(request, response, "{\"id\":1,\"userEmail\":\"arsenjeva.ea@gmail.com\"}", 201);

        assertEquals(1, appender.list.size());
        ILoggingEvent event = appender.list.getFirst();
        Map<String, String> mdc = event.getMDCPropertyMap();

        assertEquals("POST", mdc.get("http.method"));
        assertEquals("/api/v1/user", mdc.get("http.uri"));
        assertEquals("201", mdc.get("http.status"));
        assertNotNull(mdc.get("http.duration_ms"));
        assertNotNull(mdc.get("correlation_id"));

        // Тела логируются на INFO и маскируются
        String requestBody = mdc.get("http.request_body");
        assertNotNull(requestBody);
        assertTrue(requestBody.contains("\"password\":\"***\""), requestBody);
        assertFalse(requestBody.contains("secret123"), requestBody);
        assertFalse(requestBody.contains("user@example.com"), requestBody);
        assertEquals("{\"email\":\"***\",\"password\":\"***\"}", requestBody);

        String responseBody = mdc.get("http.response_body");
        assertNotNull(responseBody);
        assertFalse(responseBody.contains("arsenjeva.ea@gmail.com"), responseBody);
        assertTrue(responseBody.contains("\"userEmail\":\"***\""), responseBody);

        // Тела видны и в сообщении (для консоли)
        String message = event.getFormattedMessage();
        assertTrue(message.contains("request_body="), message);
        assertFalse(message.contains("secret123"), message);
    }

    @Test
    void authorizationTokenNeverAppearsInLog() throws Exception {
        httpLogger.setLevel(Level.INFO);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/categories");
        request.addHeader("Authorization", "Bearer super-secret-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        runFilter(request, response, "", 200);

        ILoggingEvent event = appender.list.getFirst();
        Map<String, String> mdc = event.getMDCPropertyMap();
        assertFalse(mdc.containsKey("authorization"));
        assertFalse(mdc.containsValue("Bearer super-secret-token"));
        assertFalse(event.getFormattedMessage().contains("super-secret-token"));
    }

    @Test
    void masksSensitiveDataInsideBodies() throws Exception {
        httpLogger.setLevel(Level.INFO);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/login");
        request.setContentType("application/x-www-form-urlencoded");
        request.setContent("username=john&password=secret123&token=abc.def.ghi".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();

        runFilter(request, response, "", 200);

        String requestBody = appender.list.getFirst().getMDCPropertyMap().get("http.request_body");
        assertNotNull(requestBody);
        assertFalse(requestBody.contains("secret123"), requestBody);
        assertFalse(requestBody.contains("abc.def.ghi"), requestBody);
        assertTrue(requestBody.contains("password=***"), requestBody);
    }

    @Test
    void bodiesAreTruncatedToLimit() throws Exception {
        httpLogger.setLevel(Level.INFO);

        String bigRequestBody = "a".repeat(700);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/spending");
        request.setContentType("application/json");
        request.setContent(bigRequestBody.getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();

        runFilter(request, response, "{\"ok\":true}", 200);

        String loggedRequest = appender.list.getFirst().getMDCPropertyMap().get("http.request_body");
        assertNotNull(loggedRequest);
        assertTrue(loggedRequest.endsWith("... [truncated]"));
        assertEquals(500 + "... [truncated]".length(), loggedRequest.length());
    }

    @Test
    void multipartBodyIsNotLogged() throws Exception {
        httpLogger.setLevel(Level.INFO);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/upload");
        request.setContentType("multipart/form-data; boundary=xyz");
        request.setContent("binary-payload".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();

        runFilter(request, response, "", 200);

        String requestBody = appender.list.getFirst().getMDCPropertyMap().get("http.request_body");
        assertEquals("<not logged>", requestBody);
    }

    @Test
    void correlationIdHeaderMatchesLogAndMdcIsCleared() throws Exception {
        httpLogger.setLevel(Level.INFO);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/categories");
        MockHttpServletResponse response = new MockHttpServletResponse();

        runFilter(request, response, "", 200);

        String header = response.getHeader("X-Correlation-Id");
        assertNotNull(header);
        assertEquals(header, appender.list.getFirst().getMDCPropertyMap().get("correlation_id"));
        assertNull(MDC.get("correlation_id"));
    }

    private void runFilter(MockHttpServletRequest request,
                           MockHttpServletResponse response,
                           String responseBody,
                           int status) throws Exception {
        FilterChain chain = (req, res) -> {
            // читаем тело запроса, чтобы ContentCachingRequestWrapper его закешировал
            req.getInputStream().readAllBytes();
            res.setContentType("application/json");
            ((HttpServletResponse) res).setStatus(status);
            res.getWriter().write(responseBody);
        };

        filter.doFilter(request, response, chain);
    }
}
