package spending.tracker.backend.model;

import lombok.Data;

@Data
public class MenuIngredientModel {
    private Long id;
    private String name;
    private Long baseMeasureId;
    private String baseMeasureCode;
}