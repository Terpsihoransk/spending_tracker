package spending.tracker.backend.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuShoppingListItemModel {
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal totalBaseUnits;
    private String baseMeasureCode;
    private String displayValue;
}