package spending.tracker.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spending.tracker.backend.service.UserService;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class EmailAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public EmailAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String email = request.getHeader("X-User-Email");
        log.debug("Email from header: {}", email);

        if (email != null && !email.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Checking if user exists: {}", email);
            if (userService.userExists(email)) {
                log.debug("User exists, setting authentication for: {}", email);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.debug("User does not exist: {}", email);
            }
        } else {
            log.debug("No email header or already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}