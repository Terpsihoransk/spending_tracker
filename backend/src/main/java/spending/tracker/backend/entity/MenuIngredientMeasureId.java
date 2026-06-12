package spending.tracker.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class MenuIngredientMeasureId implements Serializable {

    private Long ingredient;
    private Long measure;

    public MenuIngredientMeasureId() {}

    public MenuIngredientMeasureId(Long ingredient, Long measure) {
        this.ingredient = ingredient;
        this.measure = measure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuIngredientMeasureId that = (MenuIngredientMeasureId) o;
        return Objects.equals(ingredient, that.ingredient) &&
                Objects.equals(measure, that.measure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, measure);
    }
}