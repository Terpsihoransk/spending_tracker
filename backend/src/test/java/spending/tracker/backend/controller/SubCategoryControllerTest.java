package spending.tracker.backend.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import spending.tracker.backend.base.BaseSpringBootTest;
import spending.tracker.backend.util.TestDataUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SubCategoryControllerTest extends BaseSpringBootTest {

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    @SneakyThrows
    @DisplayName("должен создать подкатегорию для пользователя")
    public void testCreateSubCategory() {
        var user = testDataUtils.createUser("subcat-test@example.com", "sheet123");
        var category = testDataUtils.createCategory(user, "Food");

        String subCategoryJson = "{\"categoryId\":" + category.getId() + ",\"name\":\"Groceries\"}";
        mockMvc.perform(post("/api/v1/subcategories")
                        .header(X_USER_EMAIL_HEADER, "subcat-test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subCategoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Groceries"))
                .andExpect(jsonPath("$.categoryId").value(category.getId()))
                .andExpect(jsonPath("$.categoryName").value("Food"));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 409 Conflict при создании дубликата подкатегории")
    public void testCreateDuplicateSubCategory() {
        var user = testDataUtils.createUser("subcat-dup@example.com", "sheet456");
        var category = testDataUtils.createCategory(user, "Food");
        testDataUtils.createSubCategory(user, category, "Groceries");

        String subCategoryJson = "{\"categoryId\":" + category.getId() + ",\"name\":\"Groceries\"}";
        mockMvc.perform(post("/api/v1/subcategories")
                        .header(X_USER_EMAIL_HEADER, "subcat-dup@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subCategoryJson))
                .andExpect(status().isConflict());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть все подкатегории категории")
    public void testGetAllSubCategoriesByCategory() {
        var user = testDataUtils.createUser("subcat-list@example.com", "sheet789");
        var category = testDataUtils.createCategory(user, "Food");
        testDataUtils.createSubCategory(user, category, "Groceries");
        testDataUtils.createSubCategory(user, category, "Restaurants");

        mockMvc.perform(get("/api/v1/subcategories")
                        .param("categoryId", String.valueOf(category.getId()))
                        .header(X_USER_EMAIL_HEADER, "subcat-list@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен обновить подкатегорию пользователя")
    public void testUpdateSubCategory() {
        var user = testDataUtils.createUser("subcat-update@example.com", "sheet111");
        var category = testDataUtils.createCategory(user, "Food");
        var subCategory = testDataUtils.createSubCategory(user, category, "Groceries");

        String updateJson = "{\"categoryId\":" + category.getId() + ",\"name\":\"Supermarkets\"}";
        mockMvc.perform(put("/api/v1/subcategories/" + subCategory.getId())
                        .header(X_USER_EMAIL_HEADER, "subcat-update@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Supermarkets"));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен удалить подкатегорию")
    public void testDeleteSubCategory() {
        var user = testDataUtils.createUser("subcat-delete@example.com", "sheet222");
        var category = testDataUtils.createCategory(user, "Food");
        var subCategory = testDataUtils.createSubCategory(user, category, "Groceries");

        mockMvc.perform(delete("/api/v1/subcategories/" + subCategory.getId())
                        .header(X_USER_EMAIL_HEADER, "subcat-delete@example.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 409 Conflict при удалении подкатегории используемой в spending")
    public void testDeleteSubCategoryInUse() {
        var user = testDataUtils.createUser("subcat-inuse@example.com", "sheet333");
        var category = testDataUtils.createCategory(user, "Food");
        var subCategory = testDataUtils.createSubCategory(user, category, "Groceries");
        testDataUtils.createSpendingWithSubCategory(user, category, subCategory, new java.math.BigDecimal("50.00"), "Test spending");

        mockMvc.perform(delete("/api/v1/subcategories/" + subCategory.getId())
                        .header(X_USER_EMAIL_HEADER, "subcat-inuse@example.com"))
                .andExpect(status().isConflict());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 404 Not Found при попытке доступа к чужой подкатегории")
    public void testAccessOtherUserSubCategory() {
        var user1 = testDataUtils.createUser("subcat-user1@example.com", "sheet444");
        var user2 = testDataUtils.createUser("subcat-user2@example.com", "sheet555");
        var category = testDataUtils.createCategory(user1, "Food");
        var subCategory = testDataUtils.createSubCategory(user1, category, "Groceries");

        mockMvc.perform(delete("/api/v1/subcategories/" + subCategory.getId())
                        .header(X_USER_EMAIL_HEADER, "subcat-user2@example.com"))
                .andExpect(status().isNotFound());
    }
}