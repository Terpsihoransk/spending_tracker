package spending.tracker.backend.exception;

public class ForeignKeyException extends RuntimeException {

    private final String referencedEntity;
    private final String field;

    public ForeignKeyException(String referencedEntity, String field) {
        super(String.format("Referenced entity '%s' with %s does not exist", referencedEntity, field));
        this.referencedEntity = referencedEntity;
        this.field = field;
    }

    public String getReferencedEntity() {
        return referencedEntity;
    }

    public String getField() {
        return field;
    }
}