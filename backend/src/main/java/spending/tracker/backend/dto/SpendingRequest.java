package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Spending record DTO")
public class SpendingRequest {

    @NotBlank(message = "amount is required")
    @Schema(description = "Spending amount", example = "150.00", requiredMode = REQUIRED)
    private BigDecimal amount;

    @NotBlank(message = "category is required")
    @Schema(description = "Category name", example = "food", requiredMode = REQUIRED)
    private String category;

    @Schema(description = "Spending description", example = "Lunch at restaurant", requiredMode = NOT_REQUIRED)
    private String description;
}
