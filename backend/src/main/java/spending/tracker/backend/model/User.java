package spending.tracker.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String email;

    private String googleSheetsId;

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleSheetsId() {
        return googleSheetsId;
    }

    public void setGoogleSheetsId(String googleSheetsId) {
        this.googleSheetsId = googleSheetsId;
    }
}