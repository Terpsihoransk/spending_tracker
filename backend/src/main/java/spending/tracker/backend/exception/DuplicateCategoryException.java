package spending.tracker.backend.exception;

public class DuplicateCategoryException extends RuntimeException {

    private final String categoryName;
    private final String userEmail;

    public DuplicateCategoryException(String categoryName, String userEmail) {
        super(String.format("Category '%s' already exists for user '%s'", categoryName, userEmail));
        this.categoryName = categoryName;
        this.userEmail = userEmail;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getUserEmail() {
        return userEmail;
    }
}