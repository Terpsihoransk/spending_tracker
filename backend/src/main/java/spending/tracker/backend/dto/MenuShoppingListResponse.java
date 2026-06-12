package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu shopping list response DTO")
public class MenuShoppingListResponse {

    @Schema(description = "List of shopping list items", requiredMode = REQUIRED)
    private List<MenuShoppingListItemResponse> items = new ArrayList<>();

    @Schema(description = "List of recipe IDs included in the shopping list", example = "[1, 2]", requiredMode = REQUIRED)
    private List<Long> recipeIds = new ArrayList<>();
}