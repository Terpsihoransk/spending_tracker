package spending.tracker.backend.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import spending.tracker.backend.base.BaseSpringBootTest;
import spending.tracker.backend.entity.MenuIngredient;
import spending.tracker.backend.entity.MenuMeasure;
import spending.tracker.backend.entity.MenuRecipe;
import spending.tracker.backend.util.TestDataUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuShoppingListControllerTest extends BaseSpringBootTest {

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть пустой список покупок")
    public void testGetEmptyShoppingList() {
        var user = testDataUtils.createUser("sl-empty@example.com", "sheet-sl-1");

        mockMvc.perform(get("/api/v1/menu/shopping-list")
                        .header(X_USER_EMAIL_HEADER, "sl-empty@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.recipeIds").isEmpty());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен добавить рецепт в список покупок")
    public void testAddRecipeToShoppingList() {
        var user = testDataUtils.createUser("sl-add@example.com", "sheet-sl-2");
        var recipe = testDataUtils.createMenuRecipe("Борщ", "Вкусный борщ", List.of(user));

        mockMvc.perform(post("/api/v1/menu/shopping-list/" + recipe.getId())
                        .header(X_USER_EMAIL_HEADER, "sl-add@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен добавить несколько рецептов в список покупок")
    public void testAddMultipleRecipesToShoppingList() {
        var user = testDataUtils.createUser("sl-add-multi@example.com", "sheet-sl-3");
        var recipe1 = testDataUtils.createMenuRecipe("Борщ", "Борщ", List.of(user));
        var recipe2 = testDataUtils.createMenuRecipe("Каша", "Каша", List.of(user));

        String requestJson = String.format("{\"recipeIds\":[%d, %d]}", recipe1.getId(), recipe2.getId());

        mockMvc.perform(post("/api/v1/menu/shopping-list")
                        .header(X_USER_EMAIL_HEADER, "sl-add-multi@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть суммарный список продуктов с конвертацией в граммы")
    public void testGetShoppingListWithConversion() {
        var user = testDataUtils.createUser("sl-convert@example.com", "sheet-sl-4");
        
        // Создаём меры
        MenuMeasure gramMeasure = testDataUtils.createMenuMeasure("GRAM");
        MenuMeasure pieceMeasure = testDataUtils.createMenuMeasure("PIECE");
        MenuMeasure tablespoonMeasure = testDataUtils.createMenuMeasure("TABLESPOON");
        
        // Создаём ингредиент Морковь с базовой мерой GRAM
        MenuIngredient carrot = testDataUtils.createMenuIngredient("Морковь", gramMeasure);
        
        // Добавляем коэффициенты конвертации
        testDataUtils.createMenuIngredientMeasure(carrot, gramMeasure, BigDecimal.ONE); // 1 гр = 1 гр
        testDataUtils.createMenuIngredientMeasure(carrot, pieceMeasure, new BigDecimal("150")); // 1 шт = 150 гр
        testDataUtils.createMenuIngredientMeasure(carrot, tablespoonMeasure, new BigDecimal("20")); // 1 ст.л. = 20 гр
        
        // Создаём рецепты
        MenuRecipe recipe1 = testDataUtils.createMenuRecipe("Салат", "Салат овощной", List.of(user));
        MenuRecipe recipe2 = testDataUtils.createMenuRecipe("Суп", "Суп морковный", List.of(user));
        
        // В рецепте 1: 2 шт моркови (300 гр)
        testDataUtils.addIngredientToRecipe(recipe1, carrot, pieceMeasure, new BigDecimal("2"));
        // В рецепте 2: 200 гр моркови
        testDataUtils.addIngredientToRecipe(recipe2, carrot, gramMeasure, new BigDecimal("200"));
        
        // Добавляем оба рецепта в список покупок
        testDataUtils.addRecipeToShoppingList(user, recipe1);
        testDataUtils.addRecipeToShoppingList(user, recipe2);

        mockMvc.perform(get("/api/v1/menu/shopping-list")
                        .header(X_USER_EMAIL_HEADER, "sl-convert@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].ingredientName").value("Морковь"))
                .andExpect(jsonPath("$.items[0].totalBaseUnits").value(500.00))
                .andExpect(jsonPath("$.items[0].displayValue").value("500 гр"))
                .andExpect(jsonPath("$.recipeIds").isArray());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 404 при добавлении несуществующего рецепта")
    public void testAddNonExistentRecipe() {
        var user = testDataUtils.createUser("sl-404@example.com", "sheet-sl-5");

        mockMvc.perform(post("/api/v1/menu/shopping-list/999999")
                        .header(X_USER_EMAIL_HEADER, "sl-404@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен удалить рецепт из списка покупок")
    public void testRemoveRecipeFromShoppingList() {
        var user = testDataUtils.createUser("sl-remove@example.com", "sheet-sl-6");
        var recipe = testDataUtils.createMenuRecipe("Пирог", "Яблочный пирог", List.of(user));
        testDataUtils.addRecipeToShoppingList(user, recipe);

        mockMvc.perform(delete("/api/v1/menu/shopping-list/" + recipe.getId())
                        .header(X_USER_EMAIL_HEADER, "sl-remove@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен конвертировать миллилитры в литры при отображении")
    public void testGetShoppingListMlToLitersConversion() {
        var user = testDataUtils.createUser("sl-ml@example.com", "sheet-sl-7");
        
        // Создаём меры для жидкостей
        MenuMeasure mlMeasure = testDataUtils.createMenuMeasure("ML");
        MenuMeasure literMeasure = testDataUtils.createMenuMeasure("LITER");
        MenuMeasure cupMeasure = testDataUtils.createMenuMeasure("CUP");
        
        // Создаём ингредиент Молоко с базовой мерой ML
        MenuIngredient milk = testDataUtils.createMenuIngredient("Молоко", mlMeasure);
        
        // Добавляем коэффициенты конвертации
        testDataUtils.createMenuIngredientMeasure(milk, mlMeasure, BigDecimal.ONE); // 1 мл = 1 мл
        testDataUtils.createMenuIngredientMeasure(milk, literMeasure, new BigDecimal("1000")); // 1 л = 1000 мл
        testDataUtils.createMenuIngredientMeasure(milk, cupMeasure, new BigDecimal("250")); // 1 стакан = 250 мл
        
        // Создаём рецепты
        MenuRecipe recipe1 = testDataUtils.createMenuRecipe("Каша на молоке", "Вкусная каша", List.of(user));
        MenuRecipe recipe2 = testDataUtils.createMenuRecipe("Омлет", "Нежный омлет", List.of(user));
        
        // В рецепте 1: 1 стакан молока (250 мл)
        testDataUtils.addIngredientToRecipe(recipe1, milk, cupMeasure, BigDecimal.ONE);
        // В рецепте 2: 1 л молока (1000 мл)
        testDataUtils.addIngredientToRecipe(recipe2, milk, literMeasure, BigDecimal.ONE);
        
        // Добавляем оба рецепта в список покупок
        testDataUtils.addRecipeToShoppingList(user, recipe1);
        testDataUtils.addRecipeToShoppingList(user, recipe2);

        mockMvc.perform(get("/api/v1/menu/shopping-list")
                        .header(X_USER_EMAIL_HEADER, "sl-ml@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].ingredientName").value("Молоко"))
                .andExpect(jsonPath("$.items[0].totalBaseUnits").value(1250.00))
                .andExpect(jsonPath("$.items[0].displayValue").value("1.25 л"));
    }
}