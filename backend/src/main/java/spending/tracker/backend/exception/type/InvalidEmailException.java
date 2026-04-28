package spending.tracker.backend.exception.type;

public class InvalidEmailException extends RuntimeException {
    
    public InvalidEmailException(String message) {
        super(message);
    }
}