package spending.tracker.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spending.tracker.backend.service.UserService;

import java.io.IOException;
import java.util.Collections;

@Component
public class EmailAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(EmailAuthenticationFilter.class);

    private final UserService userService;

    public EmailAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String email = request.getHeader("X-User-Email");
        logger.debug("Email from header: {}", email);

        if (email != null && !email.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Checking if user exists: {}", email);
            if (userService.userExists(email)) {
                logger.debug("User exists, setting authentication for: {}", email);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                logger.debug("User does not exist: {}", email);
            }
        } else {
            logger.debug("No email header or already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}