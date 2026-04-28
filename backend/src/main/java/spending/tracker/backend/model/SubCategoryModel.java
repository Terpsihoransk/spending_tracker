package spending.tracker.backend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubCategoryModel {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String userEmail;
}