package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu recipe creation/update request DTO")
public class MenuRecipeRequest {

    @NotBlank(message = "name is required")
    @Schema(description = "Recipe name", example = "Борщ", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "Recipe description", example = "Классический украинский борщ")
    private String description;

    @NotEmpty(message = "userEmails list cannot be empty")
    @Schema(description = "List of user emails who have access to the recipe", example = "[\"user@example.com\"]", requiredMode = REQUIRED)
    private List<String> userEmails = new ArrayList<>();

    @Valid
    @NotEmpty(message = "ingredients list cannot be empty")
    @Schema(description = "List of ingredients in the recipe", requiredMode = REQUIRED)
    private List<MenuRecipeIngredientRequest> ingredients = new ArrayList<>();
}