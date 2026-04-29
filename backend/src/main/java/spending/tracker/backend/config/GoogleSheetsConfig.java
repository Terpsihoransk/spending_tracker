package spending.tracker.backend.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

@Configuration
public class GoogleSheetsConfig {

    @Value("${google.sheets.client-id}")
    private String clientId;

    @Value("${google.sheets.client-secret}")
    private String clientSecret;

    @Value("${google.sheets.redirect-uri}")
    private String redirectUri;

    @Value("${google.sheets.scopes}")
    private List<String> scopes;

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() throws IOException {
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        clientSecrets.setWeb(new GoogleClientSecrets.Details()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUris(List.of(redirectUri)));

        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                clientSecrets,
                scopes)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
    }

    @Bean
    public Sheets.Builder sheetsBuilder() {
        return new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), null)
                .setApplicationName("Spending Tracker");
    }
}