package spending.tracker.backend.model;

import lombok.Data;

@Data
public class UserModel {
    private String email;
    private String googleSheetsId;
}
