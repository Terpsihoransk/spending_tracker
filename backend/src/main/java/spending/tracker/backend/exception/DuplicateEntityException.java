package spending.tracker.backend.exception;

public class DuplicateEntityException extends RuntimeException {

    private final String field;
    private final String value;

    public DuplicateEntityException(String field, String value) {
        super(String.format("Entity with %s '%s' already exists", field, value));
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}