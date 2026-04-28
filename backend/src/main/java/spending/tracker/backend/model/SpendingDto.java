package spending.tracker.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Spending record DTO")
public class SpendingDto {
    @Schema(description = "Unique identifier", example = "1")
    private String id;

    @Schema(description = "Spending amount", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Category name", example = "food", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    @Schema(description = "Date of spending", example = "2024-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;


    @Schema(description = "Spending description", example = "Lunch at restaurant")
    private String description;

    @Schema(description = "User email", example = "user@example.com")
    private String userEmail;
}
