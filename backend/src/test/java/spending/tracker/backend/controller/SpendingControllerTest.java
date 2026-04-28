package spending.tracker.backend.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import spending.tracker.backend.base.BaseSpringBootTest;
import spending.tracker.backend.util.TestDataUtils;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpendingControllerTest extends BaseSpringBootTest {

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть все траты пользователя")
    public void testGetAllSpending() {
        mockMvc.perform(get("/api/v1/spending")
                        .header(X_USER_EMAIL_HEADER, "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен создать трату с валидным email пользователя")
    public void testCreateSpending_withUserEmail() {
        var user = testDataUtils.createUser("spending-test@example.com", "sheet123");
        var category = testDataUtils.createCategory(user, "Food");
        Long categoryId = category.getId();

        String spendingJson = "{\"amount\":1500.50,\"categoryId\":" + categoryId + ",\"description\":\"Dinner at the restaurant\"}";
        mockMvc.perform(post("/api/v1/spending")
                        .header(X_USER_EMAIL_HEADER, "spending-test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(spendingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("spending-test@example.com"))
                .andExpect(jsonPath("$.amount").value(1500.50))
                .andExpect(jsonPath("$.categoryName").value("Food"));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен обновить трату пользователя")
    public void testUpdateSpending() {
        var user = testDataUtils.createUser("update-test@example.com", "sheet123");
        var category = testDataUtils.createCategory(user, "Food");
        var newCategory = testDataUtils.createCategory(user, "Transport");
        var spending = testDataUtils.createSpending(user, category, new BigDecimal("100.00"), "Initial");
        Long spendingId = spending.getId();

        String updateJson = "{\"amount\":200.00,\"categoryId\":" + newCategory.getId() + ",\"description\":\"Updated\"}";
        mockMvc.perform(put("/api/v1/spending/" + spendingId)
                        .header(X_USER_EMAIL_HEADER, "update-test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.categoryName").value("Transport"))
                .andExpect(jsonPath("$.description").value("Updated"))
                .andExpect(jsonPath("$.userEmail").value("update-test@example.com"));
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 404 Not Found при удалении несуществующей траты")
    public void testDeleteSpending_notFound() {
        mockMvc.perform(delete("/api/v1/spending/999")
                        .header(X_USER_EMAIL_HEADER, "test@example.com"))
                .andExpect(status().isNotFound());
    }
}