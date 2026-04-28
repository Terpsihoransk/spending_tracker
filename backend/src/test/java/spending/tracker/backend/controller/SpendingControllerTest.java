package spending.tracker.backend.controller;

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
    public void testGetAllSpending() throws Exception {
        mockMvc.perform(get("/api/v1/spending")
                        .header(X_USER_EMAIL_HEADER, "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateSpending() throws Exception {
        String json = "{\"amount\":100.00,\"category\":\"food\",\"description\":\"test\"}";

        mockMvc.perform(post("/api/v1/spending")
                        .header(X_USER_EMAIL_HEADER, "nonexistent@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateSpending_withUserEmail() throws Exception {
        String userJson = "{\"email\":\"spending-test@example.com\",\"googleSheetsId\":\"sheet123\"}";
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        String spendingJson = "{\"amount\":1500.50,\"category\":\"Food\",\"description\":\"Dinner at the restaurant\"}";
        mockMvc.perform(post("/api/v1/spending")
                        .header(X_USER_EMAIL_HEADER, "spending-test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(spendingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("spending-test@example.com"))
                .andExpect(jsonPath("$.amount").value(1500.50))
                .andExpect(jsonPath("$.category").value("Food"));
    }

    @Test
    public void testUpdateSpending() throws Exception {
        var user = testDataUtils.createUser("update-test@example.com", "sheet123");
        var spending = testDataUtils.createSpending(user, new BigDecimal("100.00"), "Food", "Initial");
        Long spendingId = spending.getId();

        String updateJson = "{\"amount\":200.00,\"category\":\"Transport\",\"description\":\"Updated\"}";
        mockMvc.perform(put("/api/v1/spending/" + spendingId)
                        .header(X_USER_EMAIL_HEADER, "update-test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.category").value("Transport"))
                .andExpect(jsonPath("$.description").value("Updated"))
                .andExpect(jsonPath("$.userEmail").value("update-test@example.com"));
    }

    @Test
    public void testDeleteSpending_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/spending/999")
                        .header(X_USER_EMAIL_HEADER, "test@example.com"))
                .andExpect(status().isNotFound());
    }
}