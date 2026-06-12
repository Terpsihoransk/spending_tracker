package spending.tracker.backend.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuRecipeModel {
    private Long id;
    private String name;
    private String description;
    private List<String> userEmails = new ArrayList<>();
    private List<MenuRecipeIngredientModel> ingredients = new ArrayList<>();
}