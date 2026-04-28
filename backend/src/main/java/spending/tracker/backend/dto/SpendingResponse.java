package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Spending record DTO")
public class SpendingResponse {

    @Schema(description = "Unique identifier", example = "1", requiredMode = REQUIRED)
    private Long id;

    @Schema(description = "Spending amount", example = "150.00", requiredMode = REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Category ID", example = "1", requiredMode = REQUIRED)
    private Long categoryId;

    @Schema(description = "Category name", example = "Food", requiredMode = REQUIRED)
    private String categoryName;

    @Schema(description = "SubCategory ID (optional)", example = "2", requiredMode = NOT_REQUIRED)
    private Long subcategoryId;

    @Schema(description = "SubCategory name (optional)", example = "Restaurants", requiredMode = NOT_REQUIRED)
    private String subcategoryName;

    @Schema(description = "Date of spending", example = "2024-01-15", requiredMode = REQUIRED)
    private LocalDate date;

    @Schema(description = "Spending description", example = "Lunch at restaurant", requiredMode = NOT_REQUIRED)
    private String description;

    @Schema(description = "User email", example = "user@example.com", requiredMode = REQUIRED)
    private String userEmail;
}
