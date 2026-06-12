package spending.tracker.backend.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import spending.tracker.backend.base.BaseSpringBootTest;
import spending.tracker.backend.util.TestDataUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuControllerTest extends BaseSpringBootTest {

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть все меры")
    public void testGetAllMeasures() {
        mockMvc.perform(get("/api/v1/menu/measures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть меру по ID")
    public void testGetMeasureById() {
        var measure = testDataUtils.createMenuMeasure("TEST_MEASURE");

        mockMvc.perform(get("/api/v1/menu/measures/" + measure.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST_MEASURE"));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть все ингредиенты")
    public void testGetAllIngredients() {
        mockMvc.perform(get("/api/v1/menu/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен создать ингредиент")
    public void testCreateIngredient() {
        var measure = testDataUtils.createMenuMeasure("GRAM_TEST");

        String requestJson = String.format("{\"name\":\"Картофель\",\"baseMeasureId\":%d}", measure.getId());

        mockMvc.perform(post("/api/v1/menu/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Картофель"))
                .andExpect(jsonPath("$.baseMeasureCode").value("GRAM_TEST"));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 409 при создании дубликата ингредиента")
    public void testCreateDuplicateIngredient() {
        var measure = testDataUtils.createMenuMeasure("GRAM_DUP");
        testDataUtils.createMenuIngredient("Лук", measure);

        String requestJson = String.format("{\"name\":\"Лук\",\"baseMeasureId\":%d}", measure.getId());

        mockMvc.perform(post("/api/v1/menu/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен удалить ингредиент")
    public void testDeleteIngredient() {
        var measure = testDataUtils.createMenuMeasure("GRAM_DEL");
        var ingredient = testDataUtils.createMenuIngredient("Чеснок", measure);

        mockMvc.perform(delete("/api/v1/menu/ingredients/" + ingredient.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 409 при удалении используемого ингредиента")
    public void testDeleteIngredientInUse() {
        var user = testDataUtils.createUser("recipe-ing-use@example.com", "sheet1");
        var measure = testDataUtils.createMenuMeasure("GRAM_USE");
        var ingredient = testDataUtils.createMenuIngredient("Томат", measure);
        var recipe = testDataUtils.createMenuRecipe("Томатный суп", "Вкусный суп", List.of(user));
        testDataUtils.addIngredientToRecipe(recipe, ingredient, measure, BigDecimal.TEN);

        mockMvc.perform(delete("/api/v1/menu/ingredients/" + ingredient.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен создать рецепт")
    public void testCreateRecipe() {
        var user = testDataUtils.createUser("recipe-create@example.com", "sheet2");
        var measure = testDataUtils.createMenuMeasure("GRAM_REC");
        var ingredient = testDataUtils.createMenuIngredient("Капуста", measure);

        String requestJson = String.format(
                "{\"name\":\"Салат\",\"description\":\"Простой салат\",\"userEmails\":[\"recipe-create@example.com\"],\"ingredients\":[{\"ingredientId\":%d,\"measureId\":%d,\"amount\":150.00}]}",
                ingredient.getId(), measure.getId()
        );

        mockMvc.perform(post("/api/v1/menu/recipes")
                        .header(X_USER_EMAIL_HEADER, "recipe-create@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Салат"))
                .andExpect(jsonPath("$.ingredients[0].ingredientName").value("Капуста"))
                .andExpect(jsonPath("$.ingredients[0].amount").value(150.00));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть все рецепты пользователя")
    public void testGetAllRecipes() {
        var user = testDataUtils.createUser("recipe-all@example.com", "sheet3");
        testDataUtils.createMenuRecipe("Плов", "Узбекский плов", List.of(user));

        mockMvc.perform(get("/api/v1/menu/recipes")
                        .header(X_USER_EMAIL_HEADER, "recipe-all@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Плов"));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть рецепт по ID")
    public void testGetRecipeById() {
        var user = testDataUtils.createUser("recipe-by-id@example.com", "sheet4");
        var recipe = testDataUtils.createMenuRecipe("Оливье", "Новогодний салат", List.of(user));

        mockMvc.perform(get("/api/v1/menu/recipes/" + recipe.getId())
                        .header(X_USER_EMAIL_HEADER, "recipe-by-id@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Оливье"));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 404 при запросе чужого рецепта")
    public void testGetRecipeByIdForbidden() {
        var user1 = testDataUtils.createUser("recipe-owner@example.com", "sheet5");
        var user2 = testDataUtils.createUser("recipe-stranger@example.com", "sheet6");
        var recipe = testDataUtils.createMenuRecipe("Секретный рецепт", "Секрет", List.of(user1));

        mockMvc.perform(get("/api/v1/menu/recipes/" + recipe.getId())
                        .header(X_USER_EMAIL_HEADER, "recipe-stranger@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен обновить рецепт")
    public void testUpdateRecipe() {
        var user = testDataUtils.createUser("recipe-update@example.com", "sheet7");
        var measure = testDataUtils.createMenuMeasure("GRAM_UPD");
        var ingredient = testDataUtils.createMenuIngredient("Рис", measure);
        var recipe = testDataUtils.createMenuRecipe("Каша", "Рисовая каша", List.of(user));
        testDataUtils.addIngredientToRecipe(recipe, ingredient, measure, BigDecimal.TEN);

        String requestJson = String.format(
                "{\"name\":\"Каша супер\",\"description\":\"Очень вкусная рисовая каша\",\"userEmails\":[\"recipe-update@example.com\"],\"ingredients\":[{\"ingredientId\":%d,\"measureId\":%d,\"amount\":20.00}]}",
                ingredient.getId(), measure.getId()
        );

        mockMvc.perform(put("/api/v1/menu/recipes/" + recipe.getId())
                        .header(X_USER_EMAIL_HEADER, "recipe-update@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Каша супер"))
                .andExpect(jsonPath("$.description").value("Очень вкусная рисовая каша"))
                .andExpect(jsonPath("$.ingredients[0].amount").value(20.00));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен удалить рецепт")
    public void testDeleteRecipe() {
        var user = testDataUtils.createUser("recipe-delete@example.com", "sheet8");
        var recipe = testDataUtils.createMenuRecipe("Суп", "Овощной суп", List.of(user));

        mockMvc.perform(delete("/api/v1/menu/recipes/" + recipe.getId())
                        .header(X_USER_EMAIL_HEADER, "recipe-delete@example.com"))
                .andExpect(status().isOk());
    }
}