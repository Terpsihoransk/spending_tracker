package spending.tracker.backend.exception.type;

import lombok.Getter;

@Getter
public class DuplicateMenuIngredientException extends RuntimeException {

    private final String ingredientName;

    public DuplicateMenuIngredientException(String ingredientName) {
        super(String.format("Ingredient '%s' already exists", ingredientName));
        this.ingredientName = ingredientName;
    }
}