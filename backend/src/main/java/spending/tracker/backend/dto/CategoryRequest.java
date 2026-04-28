package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Category creation/update request DTO")
public class CategoryRequest {

    @NotBlank(message = "name is required")
    @Schema(description = "Category name", example = "Food", requiredMode = REQUIRED)
    private String name;
}