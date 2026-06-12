package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu shopping list item response DTO")
public class MenuShoppingListItemResponse {

    @Schema(description = "Ingredient ID", example = "1", requiredMode = REQUIRED)
    private Long ingredientId;

    @Schema(description = "Ingredient name", example = "Свёкла", requiredMode = REQUIRED)
    private String ingredientName;

    @Schema(description = "Total amount in base units", example = "350.00", requiredMode = REQUIRED)
    private BigDecimal totalBaseUnits;

    @Schema(description = "Base measure code", example = "GRAM", requiredMode = REQUIRED)
    private String baseMeasureCode;

    @Schema(description = "Formatted display value", example = "350 гр", requiredMode = REQUIRED)
    private String displayValue;
}