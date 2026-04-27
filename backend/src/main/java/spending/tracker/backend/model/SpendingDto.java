package spending.tracker.backend.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SpendingDto {
    private Long id;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String description;
    private String userId;
}
