package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "User DTO")
public class UserRequest {

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User email", example = "user@example.com", requiredMode = REQUIRED)
    private String email;

    @NotBlank(message = "googleSheetsId is required")
    @Schema(description = "Google Sheets ID for data sync", example = "sheet123", requiredMode = REQUIRED)
    private String googleSheetsId;
}