package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spending.tracker.backend.dto.MenuIngredientRequest;
import spending.tracker.backend.dto.MenuIngredientResponse;
import spending.tracker.backend.entity.MenuIngredient;
import spending.tracker.backend.model.MenuIngredientModel;

@Mapper(componentModel = "spring")
public interface MenuIngredientMapper {

    @Mapping(target = "baseMeasureId", source = "baseMeasure.id")
    @Mapping(target = "baseMeasureCode", source = "baseMeasure.code")
    MenuIngredientModel toModel(MenuIngredient entity);

    @Mapping(target = "baseMeasure", ignore = true)
    MenuIngredient toEntity(MenuIngredientModel model);

    MenuIngredientResponse toDto(MenuIngredientModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "baseMeasure", ignore = true)
    MenuIngredient toEntity(MenuIngredientRequest request);
}