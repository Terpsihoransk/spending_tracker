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
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "subcategoryId", source = "subCategory.id")
    @Mapping(target = "subcategoryName", source = "subCategory.name")
    SpendingModel toModel(Spending entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    Spending toEntity(SpendingModel model);

    SpendingResponse toDto(SpendingModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEmail", source = "userEmail")
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "subcategoryName", ignore = true)
    SpendingModel toModel(SpendingRequest request, String userEmail);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEmail", source = "userEmail")
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "subcategoryName", ignore = true)
    void updateModel(SpendingRequest request, @MappingTarget SpendingModel model, String userEmail);
}
