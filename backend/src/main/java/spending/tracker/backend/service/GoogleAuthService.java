package spending.tracker.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.service.data.UserDataService;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {

    private final GoogleAuthorizationCodeFlow authorizationFlow;
    private final UserDataService userDataService;

    public String getAuthorizationUrl(String state) {
        return authorizationFlow.newAuthorizationUrl()
                .setState(state)
                .build();
    }

    public User handleCallback(String code, String email) throws IOException {
        GoogleTokenResponse tokenResponse = authorizationFlow.newTokenRequest(code).execute();

        User user = userDataService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        user.setGoogleAccessToken(tokenResponse.getAccessToken());
        user.setGoogleRefreshToken(tokenResponse.getRefreshToken());
        user.setTokenExpiry(Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()));

        return userDataService.save(user);
    }

    public String getAccessToken(User user) throws IOException {
        if (user.getTokenExpiry() == null || Instant.now().isAfter(user.getTokenExpiry())) {
            return refreshAccessToken(user);
        }
        return user.getGoogleAccessToken();
    }

    private String refreshAccessToken(User user) throws IOException {
        GoogleTokenResponse tokenResponse = authorizationFlow.newTokenRequest(user.getGoogleRefreshToken())
                .setGrantType("refresh_token")
                .execute();

        user.setGoogleAccessToken(tokenResponse.getAccessToken());
        user.setTokenExpiry(Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
        userDataService.save(user);

        log.info("Refreshed access token for user: {}", user.getEmail());
        return user.getGoogleAccessToken();
    }
}