package spending.tracker.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import spending.tracker.backend.base.BaseSpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseSpringBootTest {

    @Test
    public void testCreateUser() throws Exception {
        String json = "{\"email\":\"unique_test_" + System.currentTimeMillis() + "@example.com\",\"googleSheetsId\":\"sheet_unique_" + System.currentTimeMillis() + "\"}";

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void testCreateUser_missingEmail() throws Exception {
        String json = "{\"googleSheetsId\":\"sheet123_" + System.currentTimeMillis() + "\"}";

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUser_emptyBody() throws Exception {
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk());
    }
}
