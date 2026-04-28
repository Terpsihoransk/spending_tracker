package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Category response DTO")
public class CategoryResponse {

    @Schema(description = "Unique identifier", example = "1", requiredMode = REQUIRED)
    private Long id;

    @Schema(description = "Category name", example = "Food", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "User email", example = "user@example.com", requiredMode = REQUIRED)
    private String userEmail;
}