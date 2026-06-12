package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu shopping list add request DTO")
public class MenuShoppingListAddRequest {

    @NotEmpty(message = "recipeIds list cannot be empty")
    @Schema(description = "List of recipe IDs to add to the shopping list", example = "[1, 2]", requiredMode = REQUIRED)
    private List<Long> recipeIds = new ArrayList<>();
}