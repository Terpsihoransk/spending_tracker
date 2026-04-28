package spending.tracker.backend.model;

import lombok.Data;

@Data
public class UserModel {
    private Long id;
    private String email;
    private String googleSheetsId;
}
