package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spending.tracker.backend.dto.SpendingRequest;
import spending.tracker.backend.dto.SpendingResponse;
import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.model.SpendingModel;

@Mapper(componentModel = "spring")
public interface SpendingMapper {

    @Mapping(target = "userEmail", source = "user.email")
    SpendingModel toModel(Spending entity);

    @Mapping(target = "user", ignore = true)
    Spending toEntity(SpendingModel model);

    SpendingResponse toDto(SpendingModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    SpendingModel toModel(SpendingRequest request, String userEmail);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "userEmail", source = "userEmail")
    void updateModel(SpendingRequest request, @MappingTarget SpendingModel model, String userEmail);
}
