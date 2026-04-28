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
        String json = "{\"email\":\"test@example.com\",\"googleSheetsId\":\"sheet123\"}";

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.googleSheetsId").value("sheet123"));
    }

    @Test
    public void testCreateUser_missingEmail() throws Exception {
        String json = "{\"googleSheetsId\":\"sheet123\"}";

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.googleSheetsId").value("sheet123"));
    }

    @Test
    public void testCreateUser_emptyBody() throws Exception {
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk());
    }
}
