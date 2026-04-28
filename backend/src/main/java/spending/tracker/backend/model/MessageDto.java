package spending.tracker.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Simple message response DTO")
public class MessageDto {
    @Schema(description = "Message text", example = "Operation completed successfully", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
