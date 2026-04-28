package spending.tracker.backend.exception;

public class DuplicateSubCategoryException extends RuntimeException {

    public DuplicateSubCategoryException(String subCategoryName, String userEmail) {
        super(String.format("SubCategory '%s' already exists for user '%s'", subCategoryName, userEmail));
    }
}