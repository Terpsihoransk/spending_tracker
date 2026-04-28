package spending.tracker.backend.exception.type;

import lombok.Getter;

@Getter
public class CategoryInUseException extends RuntimeException {

    private final Long entityId;

    public CategoryInUseException(String message, Long entityId) {
        super(message);
        this.entityId = entityId;
    }

}