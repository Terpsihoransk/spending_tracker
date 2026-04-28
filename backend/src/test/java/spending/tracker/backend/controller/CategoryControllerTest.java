package spending.tracker.backend.controller;

import lombok.SneakyThrows;
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

public class CategoryControllerTest extends BaseSpringBootTest {

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    @SneakyThrows
    public void testGetAllCategories() {
        mockMvc.perform(get("/api/v1/categories")
                        .header(X_USER_EMAIL_HEADER, "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testCreateCategory() {
        String userJson = "{\"email\":\"category-test@example.com\",\"googleSheetsId\":\"sheet123\"}";
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        String categoryJson = "{\"name\":\"Food\"}";
        mockMvc.perform(post("/api/v1/categories")
                        .header(X_USER_EMAIL_HEADER, "category-test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.userEmail").value("category-test@example.com"));
    }

    @Test
    public void testCreateDuplicateCategory() throws Exception {
        String userJson = "{\"email\":\"category-dup@example.com\",\"googleSheetsId\":\"sheet456\"}";
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        String categoryJson = "{\"name\":\"Food\"}";
        mockMvc.perform(post("/api/v1/categories")
                        .header(X_USER_EMAIL_HEADER, "category-dup@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/categories")
                        .header(X_USER_EMAIL_HEADER, "category-dup@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetCategoryById() throws Exception {
        var user = testDataUtils.createUser("get-cat@example.com", "sheet789");
        var category = testDataUtils.createCategory(user, "Transport");

        mockMvc.perform(get("/api/v1/categories/" + category.getId())
                        .header(X_USER_EMAIL_HEADER, "get-cat@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Transport"))
                .andExpect(jsonPath("$.userEmail").value("get-cat@example.com"));
    }

    @Test
    public void testUpdateCategory() throws Exception {
        var user = testDataUtils.createUser("update-cat@example.com", "sheet111");
        var category = testDataUtils.createCategory(user, "Food");

        String updateJson = "{\"name\":\"Groceries\"}";
        mockMvc.perform(put("/api/v1/categories/" + category.getId())
                        .header(X_USER_EMAIL_HEADER, "update-cat@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        var user = testDataUtils.createUser("delete-cat@example.com", "sheet222");
        var category = testDataUtils.createCategory(user, "Entertainment");

        mockMvc.perform(delete("/api/v1/categories/" + category.getId())
                        .header(X_USER_EMAIL_HEADER, "delete-cat@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCategory_inUse() throws Exception {
        var user = testDataUtils.createUser("delete-cat-used@example.com", "sheet333");
        var category = testDataUtils.createCategory(user, "UsedCategory");
        testDataUtils.createSpending(user, category, java.math.BigDecimal.valueOf(100), "Test spending");

        mockMvc.perform(delete("/api/v1/categories/" + category.getId())
                        .header(X_USER_EMAIL_HEADER, "delete-cat-used@example.com"))
                .andExpect(status().isConflict());
    }
}