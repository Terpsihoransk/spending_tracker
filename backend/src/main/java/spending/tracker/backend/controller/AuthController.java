package spending.tracker.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.service.GoogleAuthService;
import spending.tracker.backend.validation.ValidEmailHeader;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Google OAuth authentication endpoints")
public class AuthController {

    private final GoogleAuthService googleAuthService;

    @GetMapping("/google")
    @Operation(summary = "Start Google OAuth authorization", description = "Returns URL for Google OAuth consent screen")
    public ResponseEntity<Map<String, String>> initiateGoogleAuth(@ValidEmailHeader String email) {
        String state = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String authUrl = googleAuthService.getAuthorizationUrl(state);
        
        log.info("Initiating Google OAuth for user: {}", email);
        
        return ResponseEntity.ok(Map.of("authorizationUrl", authUrl));
    }

    @GetMapping("/google/callback")
    @Operation(summary = "Handle Google OAuth callback", description = "Processes OAuth callback and stores tokens")
    public ResponseEntity<Map<String, Object>> handleGoogleCallback(
            @RequestParam String code,
            @RequestParam String state) throws IOException {
        
        String email = URLEncoder.decode(state, StandardCharsets.UTF_8);
        log.info("Processing OAuth callback for user: {}", email);
        
        User user = googleAuthService.handleCallback(code, email);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Google Sheets connected successfully",
                "userId", user.getId(),
                "googleSheetsId", user.getGoogleSheetsId()
        ));
    }
}