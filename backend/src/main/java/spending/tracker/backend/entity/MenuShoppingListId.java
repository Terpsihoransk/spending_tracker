package spending.tracker.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class MenuShoppingListId implements Serializable {

    private Long user;
    private Long recipe;

    public MenuShoppingListId() {}

    public MenuShoppingListId(Long user, Long recipe) {
        this.user = user;
        this.recipe = recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuShoppingListId that = (MenuShoppingListId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(recipe, that.recipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, recipe);
    }
}