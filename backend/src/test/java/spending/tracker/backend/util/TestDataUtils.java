package spending.tracker.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spending.tracker.backend.entity.Category;
import spending.tracker.backend.entity.MenuIngredient;
import spending.tracker.backend.entity.MenuIngredientMeasure;
import spending.tracker.backend.entity.MenuMeasure;
import spending.tracker.backend.entity.MenuRecipe;
import spending.tracker.backend.entity.MenuRecipeIngredient;
import spending.tracker.backend.entity.MenuShoppingList;
import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.entity.SubCategory;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.repository.CategoryRepository;
import spending.tracker.backend.repository.MenuIngredientMeasureRepository;
import spending.tracker.backend.repository.MenuIngredientRepository;
import spending.tracker.backend.repository.MenuMeasureRepository;
import spending.tracker.backend.repository.MenuRecipeIngredientRepository;
import spending.tracker.backend.repository.MenuRecipeRepository;
import spending.tracker.backend.repository.MenuShoppingListRepository;
import spending.tracker.backend.repository.SpendingRepository;
import spending.tracker.backend.repository.SubCategoryRepository;
import spending.tracker.backend.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestDataUtils {

    private final UserRepository userRepository;
    private final SpendingRepository spendingRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    private final MenuMeasureRepository menuMeasureRepository;
    private final MenuIngredientRepository menuIngredientRepository;
    private final MenuIngredientMeasureRepository menuIngredientMeasureRepository;
    private final MenuRecipeRepository menuRecipeRepository;
    private final MenuRecipeIngredientRepository menuRecipeIngredientRepository;
    private final MenuShoppingListRepository menuShoppingListRepository;

    @Autowired
    public TestDataUtils(UserRepository userRepository, SpendingRepository spendingRepository, CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository, MenuMeasureRepository menuMeasureRepository, MenuIngredientRepository menuIngredientRepository, MenuIngredientMeasureRepository menuIngredientMeasureRepository, MenuRecipeRepository menuRecipeRepository, MenuRecipeIngredientRepository menuRecipeIngredientRepository, MenuShoppingListRepository menuShoppingListRepository) {
        this.userRepository = userRepository;
        this.spendingRepository = spendingRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.menuMeasureRepository = menuMeasureRepository;
        this.menuIngredientRepository = menuIngredientRepository;
        this.menuIngredientMeasureRepository = menuIngredientMeasureRepository;
        this.menuRecipeRepository = menuRecipeRepository;
        this.menuRecipeIngredientRepository = menuRecipeIngredientRepository;
        this.menuShoppingListRepository = menuShoppingListRepository;
    }

    public User createUser(String email, String googleSheetsId) {
        User user = new User();
        user.setEmail(email);
        user.setGoogleSheetsId(googleSheetsId);
        return userRepository.save(user);
    }

    public Category createCategory(User user, String name) {
        Category category = new Category();
        category.setUser(user);
        category.setName(name);
        return categoryRepository.save(category);
    }

    public SubCategory createSubCategory(User user, Category category, String name) {
        SubCategory subCategory = new SubCategory();
        subCategory.setUser(user);
        subCategory.setCategory(category);
        subCategory.setName(name);
        return subCategoryRepository.save(subCategory);
    }

    public Spending createSpending(User user, Category category, BigDecimal amount, String description) {
        Spending spending = new Spending();
        spending.setUser(user);
        spending.setCategory(category);
        spending.setAmount(amount);
        spending.setDescription(description);
        spending.setDate(LocalDate.now());
        return spendingRepository.save(spending);
    }

    public Spending createSpendingWithSubCategory(User user, Category category, SubCategory subCategory, BigDecimal amount, String description) {
        Spending spending = new Spending();
        spending.setUser(user);
        spending.setCategory(category);
        spending.setSubCategory(subCategory);
        spending.setAmount(amount);
        spending.setDescription(description);
        spending.setDate(LocalDate.now());
        return spendingRepository.save(spending);
    }

    // --- MENU MODULE UTILS ---

    public MenuMeasure createMenuMeasure(String code) {
        var measure = new MenuMeasure();
        measure.setCode(code);
        return menuMeasureRepository.save(measure);
    }

    public MenuIngredient createMenuIngredient(String name, MenuMeasure baseMeasure) {
        var ingredient = new MenuIngredient();
        ingredient.setName(name);
        ingredient.setBaseMeasure(baseMeasure);
        return menuIngredientRepository.save(ingredient);
    }

    public MenuIngredientMeasure createMenuIngredientMeasure(MenuIngredient ingredient, MenuMeasure measure, BigDecimal baseUnitCoefficient) {
        var im = new MenuIngredientMeasure();
        im.setIngredient(ingredient);
        im.setMeasure(measure);
        im.setBaseUnitCoefficient(baseUnitCoefficient);
        return menuIngredientMeasureRepository.save(im);
    }

    public MenuRecipe createMenuRecipe(String name, String description, List<User> users) {
        var recipe = new MenuRecipe();
        recipe.setName(name);
        recipe.setDescription(description);
        recipe.setUsers(new ArrayList<>(users));
        return menuRecipeRepository.save(recipe);
    }

    public MenuRecipeIngredient addIngredientToRecipe(MenuRecipe recipe, MenuIngredient ingredient, MenuMeasure measure, BigDecimal amount) {
        var ri = new MenuRecipeIngredient();
        ri.setRecipe(recipe);
        ri.setIngredient(ingredient);
        ri.setMeasure(measure);
        ri.setAmount(amount);
        var saved = menuRecipeIngredientRepository.save(ri);
        recipe.getIngredients().add(saved);
        return saved;
    }

    public MenuShoppingList addRecipeToShoppingList(User user, MenuRecipe recipe) {
        var sl = new MenuShoppingList();
        sl.setUser(user);
        sl.setRecipe(recipe);
        return menuShoppingListRepository.save(sl);
    }
}