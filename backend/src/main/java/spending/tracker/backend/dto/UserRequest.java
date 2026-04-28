package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "User DTO")
public class UserRequest {

    @Schema(description = "User email", example = "user@example.com", requiredMode = REQUIRED)
    private String email;

    @Schema(description = "Google Sheets ID for data sync", example = "sheet123", requiredMode = NOT_REQUIRED)
    private String googleSheetsId;
}