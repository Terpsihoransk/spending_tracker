package spending.tracker.backend.model;

import lombok.Data;

@Data
public class CategoryModel {
    private Long id;
    private String name;
    private String userEmail;
}