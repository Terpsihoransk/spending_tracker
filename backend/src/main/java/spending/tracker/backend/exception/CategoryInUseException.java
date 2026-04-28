package spending.tracker.backend.exception;

public class CategoryInUseException extends RuntimeException {

    private final Long categoryId;

    public CategoryInUseException(Long categoryId) {
        super(String.format("Cannot delete category with id %d because it is referenced by existing spendings", categoryId));
        this.categoryId = categoryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}