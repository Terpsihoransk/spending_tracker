package spending.tracker.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User DTO")
public class UserDto {
    @Schema(description = "User email", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Google Sheets ID for data sync", example = "sheet123")
    private String googleSheetsId;
}