package spending.tracker.backend.logging;

import java.util.regex.Pattern;

/**
 * Централизованное маскирование sensitive-данных в текстовых логах и телах запросов/ответов.
 * <p>
 * Маскируются:
 * <ul>
 *     <li>значения JSON-полей, имя которых содержит password/passwd/pwd/token/secret/authorization/cookie/email;</li>
 *     <li>значения form-параметров с теми же ключами;</li>
 *     <li>токены вида {@code Bearer <token>};</li>
 *     <li>JWT-подобные строки ({@code <seg>.<seg>.<seg>});</li>
 *     <li>email-адреса.</li>
 * </ul>
 * Замена — на {@link #MASK} ({@code ***}).
 */
public final class LogSanitizer {

    public static final String MASK = "***";

    private static final Pattern SENSITIVE_JSON_FIELD = Pattern.compile(
            "(\"[A-Za-z0-9_]*(?:password|passwd|pwd|token|secret|authorization|cookie|email)[A-Za-z0-9_]*\"\\s*:\\s*\")[^\"]*(\")",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern SENSITIVE_FORM_FIELD = Pattern.compile(
            "(?i)(password|passwd|pwd|token|access_token|refresh_token|secret|authorization|cookie|email)=[^&\\s]*");
    private static final Pattern BEARER_TOKEN = Pattern.compile("Bearer\\s+[A-Za-z0-9._\\-]+");
    private static final Pattern JWT_TOKEN = Pattern.compile("[A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-]+");
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}");

    private LogSanitizer() {
    }

    /**
     * Маскирует sensitive-данные в произвольном тексте. {@code null} возвращается как есть.
     */
    public static String mask(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String result = SENSITIVE_JSON_FIELD.matcher(text).replaceAll("$1" + MASK + "$2");
        result = SENSITIVE_FORM_FIELD.matcher(result).replaceAll("$1=" + MASK);
        result = BEARER_TOKEN.matcher(result).replaceAll("Bearer " + MASK);
        result = JWT_TOKEN.matcher(result).replaceAll(MASK);
        result = EMAIL.matcher(result).replaceAll(MASK);
        return result;
    }
}
