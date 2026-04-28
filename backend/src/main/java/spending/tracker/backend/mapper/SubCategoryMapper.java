package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spending.tracker.backend.dto.SubCategoryRequest;
import spending.tracker.backend.dto.SubCategoryResponse;
import spending.tracker.backend.entity.SubCategory;
import spending.tracker.backend.model.SubCategoryModel;

@Mapper(componentModel = "spring")
public interface SubCategoryMapper {

    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryId", source = "category.id")
    SubCategoryModel toModel(SubCategory entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    SubCategory toEntity(SubCategoryModel model);

    SubCategoryResponse toDto(SubCategoryModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    SubCategory toEntity(SubCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    void updateModel(SubCategoryRequest request, @MappingTarget SubCategoryModel model);
}