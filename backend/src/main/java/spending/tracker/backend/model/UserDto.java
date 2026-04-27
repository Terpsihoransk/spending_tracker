package spending.tracker.backend.model;

import lombok.Data;

@Data
public class UserDto {
    private String email;
    private String googleSheetsId;
}