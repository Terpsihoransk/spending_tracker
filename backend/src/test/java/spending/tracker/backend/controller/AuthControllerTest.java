package spending.tracker.backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.service.GoogleAuthService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private GoogleAuthService googleAuthService;

    @InjectMocks
    private AuthController authController;

    @Test
    void initiateGoogleAuth_ShouldReturnAuthorizationUrl() {
        String email = "test@example.com";
        String expectedUrl = "https://accounts.google.com/o/oauth2/auth?client_id=xxx";
        
        when(googleAuthService.getAuthorizationUrl(anyString())).thenReturn(expectedUrl);

        ResponseEntity<Map<String, String>> response = authController.initiateGoogleAuth(email);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedUrl, response.getBody().get("authorizationUrl"));
        verify(googleAuthService, times(1)).getAuthorizationUrl(anyString());
    }

    @Test
    void handleGoogleCallback_ShouldReturnSuccessResponse() throws Exception {
        String code = "auth_code_123";
        String state = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(state);
        user.setGoogleSheetsId("spreadsheet_id_123");

        when(googleAuthService.handleCallback(code, state)).thenReturn(user);

        ResponseEntity<Map<String, Object>> response = authController.handleGoogleCallback(code, state);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Google Sheets connected successfully", response.getBody().get("message"));
        assertEquals(1L, response.getBody().get("userId"));
        assertEquals("spreadsheet_id_123", response.getBody().get("googleSheetsId"));
        verify(googleAuthService, times(1)).handleCallback(code, state);
    }
}