package spending.tracker.backend.validation;

import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import spending.tracker.backend.exception.type.InvalidEmailException;

import java.util.regex.Pattern;

@Component
public class ValidEmailHeaderArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEFAULT_HEADER_NAME = "X-User-Email";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ValidEmailHeader.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        String email = webRequest.getHeader(DEFAULT_HEADER_NAME);
        
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("X-User-Email header is required");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
        
        return email;
    }
}