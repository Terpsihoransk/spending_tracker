package spending.tracker.backend.exception;

public class SubCategoryInUseException extends RuntimeException {

    public SubCategoryInUseException(String subCategoryName) {
        super(String.format("Cannot delete subCategory '%s' because it is used by existing spendings", subCategoryName));
    }
}