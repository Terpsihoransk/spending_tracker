package spending.tracker.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class MenuRecipeIngredientId implements Serializable {

    private Long recipe;
    private Long ingredient;

    public MenuRecipeIngredientId() {}

    public MenuRecipeIngredientId(Long recipe, Long ingredient) {
        this.recipe = recipe;
        this.ingredient = ingredient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuRecipeIngredientId that = (MenuRecipeIngredientId) o;
        return Objects.equals(recipe, that.recipe) &&
                Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipe, ingredient);
    }
}