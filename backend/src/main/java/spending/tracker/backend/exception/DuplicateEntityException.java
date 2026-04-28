package spending.tracker.backend.exception;

import lombok.Getter;

@Getter
public class DuplicateEntityException extends RuntimeException {

    private final String field;
    private final String value;

    public DuplicateEntityException(String field, String value) {
        super(String.format("Entity with %s '%s' already exists", field, value));
        this.field = field;
        this.value = value;
    }

}