package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "SubCategory response DTO")
public class SubCategoryResponse {

    @Schema(description = "SubCategory ID", example = "1", requiredMode = REQUIRED)
    private Long id;

    @Schema(description = "SubCategory name", example = "Restaurants", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "Category ID", example = "1", requiredMode = REQUIRED)
    private Long categoryId;

    @Schema(description = "Category name", example = "Food", requiredMode = NOT_REQUIRED)
    private String categoryName;
}