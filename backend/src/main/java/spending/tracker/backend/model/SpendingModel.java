package spending.tracker.backend.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SpendingModel {
    private Long id;
    private BigDecimal amount;
    private Long categoryId;
    private String categoryName;
    private LocalDate date;
    private String description;
    private String userEmail;
}
