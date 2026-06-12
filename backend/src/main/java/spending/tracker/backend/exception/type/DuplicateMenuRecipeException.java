package spending.tracker.backend.exception.type;

import lombok.Getter;

@Getter
public class DuplicateMenuRecipeException extends RuntimeException {

    private final String recipeName;

    public DuplicateMenuRecipeException(String recipeName) {
        super(String.format("Recipe '%s' already exists", recipeName));
        this.recipeName = recipeName;
    }
}