package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "SubCategory creation/update request DTO")
public class SubCategoryRequest {

    @NotBlank(message = "name is required")
    @Schema(description = "SubCategory name", example = "Restaurants", requiredMode = REQUIRED)
    private String name;

    @NotNull(message = "categoryId is required")
    @Schema(description = "Category ID", example = "1", requiredMode = REQUIRED)
    private Long categoryId;
}