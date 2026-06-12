package spending.tracker.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Menu measure response DTO")
public class MenuMeasureResponse {

    @Schema(description = "Unique identifier", example = "1", requiredMode = REQUIRED)
    private Long id;

    @Schema(description = "Measure code", example = "GRAM", requiredMode = REQUIRED)
    private String code;
}