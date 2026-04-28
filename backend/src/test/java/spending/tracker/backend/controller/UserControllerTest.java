package spending.tracker.backend.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import spending.tracker.backend.base.BaseSpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseSpringBootTest {

    @Test
    @SneakyThrows
    @DisplayName("должен создать пользователя с валидными email и googleSheetsId")
    public void testCreateUser() {
        String json = "{\"email\":\"unique_test_" + System.currentTimeMillis() + "@example.com\",\"googleSheetsId\":\"sheet_unique_" + System.currentTimeMillis() + "\"}";

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 400 Bad Request когда email отсутствует")
    public void testCreateUser_missingEmail() {
        String json = "{\"googleSheetsId\":\"sheet123_" + System.currentTimeMillis() + "\"}";

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть 400 Bad Request когда тело запроса пустое")
    public void testCreateUser_emptyBody() {
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть список всех пользователей")
    public void testGetAllUsers() {
        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk());
    }
}
