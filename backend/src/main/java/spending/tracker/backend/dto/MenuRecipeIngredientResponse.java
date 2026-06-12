package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu recipe ingredient response DTO")
public class MenuRecipeIngredientResponse {

    @Schema(description = "Ingredient ID", example = "1", requiredMode = REQUIRED)
    private Long ingredientId;

    @Schema(description = "Ingredient name", example = "Свёкла", requiredMode = REQUIRED)
    private String ingredientName;

    @Schema(description = "Measure ID", example = "1", requiredMode = REQUIRED)
    private Long measureId;

    @Schema(description = "Measure code", example = "GRAM", requiredMode = REQUIRED)
    private String measureCode;

    @Schema(description = "Amount of ingredient", example = "200.00", requiredMode = REQUIRED)
    private BigDecimal amount;
}