package spending.tracker.backend.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuRecipeIngredientModel {
    private Long ingredientId;
    private String ingredientName;
    private Long measureId;
    private String measureCode;
    private BigDecimal amount;
}