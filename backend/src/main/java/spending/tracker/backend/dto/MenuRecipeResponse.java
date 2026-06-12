package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu recipe response DTO")
public class MenuRecipeResponse {

    @Schema(description = "Unique identifier", example = "1", requiredMode = REQUIRED)
    private Long id;

    @Schema(description = "Recipe name", example = "Борщ", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "Recipe description", example = "Классический украинский борщ")
    private String description;

    @Schema(description = "List of user emails who have access to the recipe", example = "[\"user@example.com\"]", requiredMode = REQUIRED)
    private List<String> userEmails = new ArrayList<>();

    @Schema(description = "List of ingredients in the recipe", requiredMode = REQUIRED)
    private List<MenuRecipeIngredientResponse> ingredients = new ArrayList<>();
}