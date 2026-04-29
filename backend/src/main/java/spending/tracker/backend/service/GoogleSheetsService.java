package spending.tracker.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.model.SpendingModel;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleSheetsService {

    private final GoogleAuthorizationCodeFlow authorizationFlow;

    private static final String SPENDING_SHEET_NAME = "Расходы";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Sheets getSheetsService(String accessToken) {
        return new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), 
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
                .setApplicationName("Spending Tracker")
                .build();
    }

    public void appendSpending(SpendingModel spending) throws IOException {
        String spreadsheetId = spending.getUser().getGoogleSheetsId();
        String accessToken = spending.getUser().getGoogleAccessToken();
        Sheets sheets = getSheetsService(accessToken);
        
        List<List<Object>> rows = List.of(List.of(
                spending.getId(),
                spending.getDate().format(DATE_FORMATTER),
                spending.getCategoryName(),
                spending.getSubCategoryName(),
                spending.getAmount().toString(),
                spending.getDescription() != null ? spending.getDescription() : ""
        ));

        ValueRange body = new ValueRange().setValues(rows);
        
        AppendValuesResponse response = sheets.spreadsheets().values()
                .append(spreadsheetId, SPENDING_SHEET_NAME + "!A:F", body)
                .setValueInputOption("USER_ENTERED")
                .execute();

        log.info("Appended spending {} to spreadsheet {}. Updated range: {}", 
                spending.getId(), spreadsheetId, response.getUpdates().getUpdatedRange());
    }

    public void updateSpending(SpendingModel spending) throws IOException {
        String spreadsheetId = spending.getUser().getGoogleSheetsId();
        
        Optional<Integer> rowIndex = findRowById(spreadsheetId, spending.getUser().getGoogleAccessToken(), spending.getId());
        
        if (rowIndex.isEmpty()) {
            log.warn("Spending {} not found in spreadsheet, appending instead", spending.getId());
            appendSpending(spending);
            return;
        }

        String range = SPENDING_SHEET_NAME + "!A" + rowIndex.get() + ":F" + rowIndex.get();
        
        List<List<Object>> rows = List.of(List.of(
                spending.getId(),
                spending.getDate().format(DATE_FORMATTER),
                spending.getCategoryName(),
                spending.getSubCategoryName(),
                spending.getAmount().toString(),
                spending.getDescription() != null ? spending.getDescription() : ""
        ));

        ValueRange body = new ValueRange().setValues(rows);
        
        Sheets sheets = getSheetsService(spending.getUser().getGoogleAccessToken());
        sheets.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();

        log.info("Updated spending {} in spreadsheet {}", spending.getId(), spreadsheetId);
    }

    public void deleteSpending(String spreadsheetId, String accessToken, Long spendingId) throws IOException {
        Optional<Integer> rowIndex = findRowById(spreadsheetId, accessToken, spendingId);
        
        if (rowIndex.isEmpty()) {
            log.warn("Spending {} not found in spreadsheet", spendingId);
            return;
        }

        String range = SPENDING_SHEET_NAME + "!A" + rowIndex.get() + ":F" + rowIndex.get();
        
        List<List<Object>> emptyRow = List.of(List.of("", "", "", "", "", ""));
        
        ValueRange body = new ValueRange().setValues(emptyRow);
        
        Sheets sheets = getSheetsService(accessToken);
        sheets.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();

        log.info("Deleted spending {} from spreadsheet {}", spendingId, spreadsheetId);
    }

    private Optional<Integer> findRowById(String spreadsheetId, String accessToken, Long id) throws IOException {
        Sheets sheets = getSheetsService(accessToken);
        ValueRange response = sheets.spreadsheets().values()
                .get(spreadsheetId, SPENDING_SHEET_NAME + "!A:A")
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null) {
            return Optional.empty();
        }

        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (!row.isEmpty() && row.get(0).toString().equals(id.toString())) {
                return Optional.of(i + 1);
            }
        }

        return Optional.empty();
    }

    public List<List<Object>> getAllSpendings(String spreadsheetId, String accessToken) throws IOException {
        Sheets sheets = getSheetsService(accessToken);
        ValueRange response = sheets.spreadsheets().values()
                .get(spreadsheetId, SPENDING_SHEET_NAME + "!A:F")
                .execute();

        return response.getValues() != null ? response.getValues() : new ArrayList<>();
    }
}