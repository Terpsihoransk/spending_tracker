package spending.tracker.backend.exception.type;

import lombok.Getter;

@Getter
public class ForeignKeyException extends RuntimeException {

    private final String referencedEntity;
    private final String field;

    public ForeignKeyException(String referencedEntity, String field) {
        super(String.format("Referenced entity '%s' with %s does not exist", referencedEntity, field));
        this.referencedEntity = referencedEntity;
        this.field = field;
    }

}