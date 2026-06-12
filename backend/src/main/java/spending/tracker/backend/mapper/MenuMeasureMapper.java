package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import spending.tracker.backend.dto.MenuMeasureResponse;
import spending.tracker.backend.entity.MenuMeasure;
import spending.tracker.backend.model.MenuMeasureModel;

@Mapper(componentModel = "spring")
public interface MenuMeasureMapper {

    MenuMeasureModel toModel(MenuMeasure entity);

    MenuMeasure toEntity(MenuMeasureModel model);

    MenuMeasureResponse toDto(MenuMeasureModel model);
}