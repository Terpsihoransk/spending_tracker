package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu recipe ingredient request DTO")
public class MenuRecipeIngredientRequest {

    @NotNull(message = "ingredientId is required")
    @Schema(description = "Ingredient ID", example = "1", requiredMode = REQUIRED)
    private Long ingredientId;

    @NotNull(message = "measureId is required")
    @Schema(description = "Measure ID", example = "1", requiredMode = REQUIRED)
    private Long measureId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    @Schema(description = "Amount of ingredient", example = "200.00", requiredMode = REQUIRED)
    private BigDecimal amount;
}