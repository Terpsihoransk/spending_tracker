package spending.tracker.backend.exception.type;

import lombok.Getter;

@Getter
public class MenuIngredientInUseException extends RuntimeException {

    private final Long ingredientId;

    public MenuIngredientInUseException(Long ingredientId) {
        super(String.format("Ingredient with ID '%d' is in use and cannot be deleted", ingredientId));
        this.ingredientId = ingredientId;
    }
}