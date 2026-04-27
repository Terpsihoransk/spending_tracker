package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.model.SpendingDto;
import spending.tracker.backend.model.SpendingModel;

@Mapper(componentModel = "spring")
public interface SpendingMapper {

    @Mapping(source = "user.email", target = "userId")
    SpendingModel toModel(Spending entity);

    @Mapping(source = "userId", target = "user.email")
    Spending toEntity(SpendingModel model);

    SpendingDto toDto(SpendingModel model);

    SpendingModel toModel(SpendingDto dto);

    void updateModel(SpendingDto dto, @MappingTarget SpendingModel model);
}
