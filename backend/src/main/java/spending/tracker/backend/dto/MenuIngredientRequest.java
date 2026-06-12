package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu ingredient creation request DTO")
public class MenuIngredientRequest {

    @NotBlank(message = "name is required")
    @Schema(description = "Ingredient name", example = "Свёкла", requiredMode = REQUIRED)
    private String name;

    @NotNull(message = "baseMeasureId is required")
    @Schema(description = "Base measure ID", example = "1", requiredMode = REQUIRED)
    private Long baseMeasureId;
}