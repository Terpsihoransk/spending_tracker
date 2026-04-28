package spending.tracker.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response DTO")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404", requiredMode = REQUIRED)
    private int status;

    @Schema(description = "Error type/code", example = "RESOURCE_NOT_FOUND", requiredMode = REQUIRED)
    private String error;

    @Schema(description = "Human-readable error message", example = "Spending not found with id: 1", requiredMode = REQUIRED)
    private String message;

    @Schema(description = "Request path", example = "/api/v1/spending/1", requiredMode = NOT_REQUIRED)
    private String path;

    @Schema(description = "Timestamp of the error", example = "2024-01-15T10:30:00", requiredMode = NOT_REQUIRED)
    private LocalDateTime timestamp;

    @Schema(description = "Validation errors (field → error message)", example = "{\"amount\": \"must be positive\"}", requiredMode = NOT_REQUIRED)
    private Map<String, String> validationErrors;

    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}