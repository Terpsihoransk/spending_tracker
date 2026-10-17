package spending.tracker.backend.filter;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.status.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Проверяет, что Graylog-энкодер формирует валидный GELF (version/host/short_message),
 * а структурированные поля (correlation_id, http.*) и статические поля попадают
 * в сообщение как дополнительные GELF-поля. Конфигурация валидна (нет ERROR).
 */
public class GelfEncodingTest {

    private static final String CONFIG = "logback-gelf-verify.xml";
    private static final Path OUTPUT = Path.of("target/gelf-verify.json");

    private LoggerContext context;

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(OUTPUT);

        context = new LoggerContext();
        context.setMDCAdapter(new LogbackMDCAdapter());
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);

        try (InputStream config = getClass().getClassLoader().getResourceAsStream(CONFIG)) {
            assertNotNull(config, CONFIG + " должен быть в classpath");
            configurator.doConfigure(config);
        }
    }

    @AfterEach
    void tearDown() {
        context.stop();
        context.getMDCAdapter().clear();
        MDC.clear();
    }

    @Test
    void configurationHasNoErrors() {
        boolean hasError = context.getStatusManager().getCopyOfStatusList().stream()
                .anyMatch(status -> status.getLevel() == Status.ERROR);
        assertFalse(hasError, () -> context.getStatusManager().getCopyOfStatusList().toString());
    }

    @Test
    void producesValidGelfEnvelope() throws Exception {
        String output = logAndRead("call");

        assertTrue(output.contains("\"version\":\"1.1\""), output);
        assertTrue(output.contains("\"host\""), output);
        assertTrue(output.contains("\"short_message\":\"call\""), output);
    }

    @Test
    void includesStructuredFieldsFromMdcAndStatics() throws Exception {
        putMdc("correlation_id", "abc-123");
        putMdc("http.method", "GET");
        putMdc("http.status", "200");

        String output = logAndRead("request");

        assertTrue(output.contains("\"_correlation_id\":\"abc-123\""), output);
        assertTrue(output.contains("\"_http.method\":\"GET\""), output);
        assertTrue(output.contains("\"_http.status\""), output);
        assertTrue(output.contains("\"_environment\":\"production\""), output);
        assertTrue(output.contains("\"_timezone\""), output);
        assertTrue(output.contains("Novosibirsk"), output);
    }

    private void putMdc(String key, String value) {
        MDC.put(key, value);
        context.getMDCAdapter().put(key, value);
    }

    private String logAndRead(String message) throws Exception {
        context.getLogger("gelf-test").info(message);
        String output = Files.readString(OUTPUT);
        if (output.isEmpty()) {
            throw new AssertionError("Empty GELF output. Logback statuses: "
                    + context.getStatusManager().getCopyOfStatusList());
        }
        return output;
    }
}
