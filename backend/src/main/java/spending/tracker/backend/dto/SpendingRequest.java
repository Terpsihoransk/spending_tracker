package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Spending record DTO")
public class SpendingRequest {

    @NotNull(message = "amount is required")
    @Schema(description = "Spending amount", example = "150.00", requiredMode = REQUIRED)
    private BigDecimal amount;

    @NotNull(message = "categoryId is required")
    @Schema(description = "Category ID", example = "1", requiredMode = REQUIRED)
    private Long categoryId;

    @Schema(description = "SubCategory ID (optional)", example = "2", requiredMode = NOT_REQUIRED)
    private Long subcategoryId;

    @Schema(description = "Spending description", example = "Lunch at restaurant", requiredMode = NOT_REQUIRED)
    private String description;

    @Schema(description = "Spending date (optional, defaults to current date if not provided)", example = "2024-01-15", requiredMode = NOT_REQUIRED)
    private LocalDate date;
}
