package spending.tracker.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import spending.tracker.backend.base.BaseSpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpendingControllerTest extends BaseSpringBootTest {

    @Test
    public void testGetAllSpending() throws Exception {
        mockMvc.perform(get("/api/v1/spending")
                        .header("X-User-Email", "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateSpending() throws Exception {
        String json = "{\"amount\":100.00,\"category\":\"food\",\"date\":\"2024-01-15\",\"description\":\"test\"}";

        mockMvc.perform(post("/api/v1/spending")
                        .header("X-User-Email", "nonexistent@example.com")
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

        String spendingJson = "{\"amount\":1500.50,\"category\":\"Food\",\"date\":\"2024-04-27\",\"description\":\"Dinner at the restaurant\"}";
        mockMvc.perform(post("/api/v1/spending")
                        .header("X-User-Email", "spending-test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(spendingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("spending-test@example.com"))
                .andExpect(jsonPath("$.amount").value(1500.50))
                .andExpect(jsonPath("$.category").value("Food"));
    }

    @Test
    public void testDeleteSpending_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/spending/999"))
                .andExpect(status().isNotFound());
    }
}