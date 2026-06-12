package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu ingredient response DTO")
public class MenuIngredientResponse {

    @Schema(description = "Unique identifier", example = "1", requiredMode = REQUIRED)
    private Long id;

    @Schema(description = "Ingredient name", example = "Свёкла", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "Base measure ID", example = "1", requiredMode = REQUIRED)
    private Long baseMeasureId;

    @Schema(description = "Base measure code", example = "GRAM", requiredMode = REQUIRED)
    private String baseMeasureCode;
}