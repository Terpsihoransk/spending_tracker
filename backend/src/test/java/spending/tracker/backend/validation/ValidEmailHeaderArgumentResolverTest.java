package spending.tracker.backend.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import spending.tracker.backend.exception.InvalidEmailException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ValidEmailHeaderArgumentResolver Tests")
class ValidEmailHeaderArgumentResolverTest {

    private ValidEmailHeaderArgumentResolver resolver;
    private NativeWebRequest webRequest;
    private MethodParameter methodParameter;
    private ModelAndViewContainer mavContainer;
    private WebDataBinderFactory binderFactory;

    @BeforeEach
    void setUp() {
        resolver = new ValidEmailHeaderArgumentResolver();
        webRequest = mock(NativeWebRequest.class);
        methodParameter = mock(MethodParameter.class);
        mavContainer = new ModelAndViewContainer();
        binderFactory = mock(WebDataBinderFactory.class);
    }

    @Test
    @DisplayName("supportsParameter returns true when annotation is present")
    void supportsParameter_withValidEmailHeaderAnnotation_returnsTrue() {
        when(methodParameter.hasParameterAnnotation(ValidEmailHeader.class)).thenReturn(true);

        assertTrue(resolver.supportsParameter(methodParameter));
    }

    @Test
    @DisplayName("supportsParameter returns false when annotation is absent")
    void supportsParameter_withoutAnnotation_returnsFalse() {
        when(methodParameter.hasParameterAnnotation(ValidEmailHeader.class)).thenReturn(false);

        assertFalse(resolver.supportsParameter(methodParameter));
    }

    @Test
    @DisplayName("resolveArgument returns email when valid")
    void resolveArgument_withValidEmail_returnsEmail() {
        String validEmail = "user@example.com";
        when(webRequest.getHeader("X-User-Email")).thenReturn(validEmail);

        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        assertEquals(validEmail, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test.email@domain.com",
            "user+tag@subdomain.domain.org",
            "user123@test.co.uk"
    })
    @DisplayName("resolveArgument accepts various valid email formats")
    void resolveArgument_withVariousValidEmails_returnsEmail(String email) {
        when(webRequest.getHeader("X-User-Email")).thenReturn(email);

        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        assertEquals(email, result);
    }

    @Test
    @DisplayName("resolveArgument throws exception when header is missing")
    void resolveArgument_withoutHeader_throwsException() {
        when(webRequest.getHeader("X-User-Email")).thenReturn(null);

        InvalidEmailException exception = assertThrows(
                InvalidEmailException.class,
                () -> resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory)
        );

        assertEquals("X-User-Email header is required", exception.getMessage());
    }

    @Test
    @DisplayName("resolveArgument throws exception when header is blank")
    void resolveArgument_withBlankHeader_throwsException() {
        when(webRequest.getHeader("X-User-Email")).thenReturn("   ");

        InvalidEmailException exception = assertThrows(
                InvalidEmailException.class,
                () -> resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory)
        );

        assertEquals("X-User-Email header is required", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "not-an-email",
            "@nodomain.com",
            "no@domain",
            "spaces in@email.com",
            "user@domain.c"
    })
    @DisplayName("resolveArgument throws exception for invalid email formats")
    void resolveArgument_withInvalidEmail_throwsException(String invalidEmail) {
        when(webRequest.getHeader("X-User-Email")).thenReturn(invalidEmail);

        InvalidEmailException exception = assertThrows(
                InvalidEmailException.class,
                () -> resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory)
        );

        assertEquals("Invalid email format: " + invalidEmail, exception.getMessage());
    }
}