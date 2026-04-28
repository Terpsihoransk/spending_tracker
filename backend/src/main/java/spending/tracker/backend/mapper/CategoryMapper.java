package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spending.tracker.backend.dto.CategoryRequest;
import spending.tracker.backend.dto.CategoryResponse;
import spending.tracker.backend.entity.Category;
import spending.tracker.backend.model.CategoryModel;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "userEmail", source = "user.email")
    CategoryModel toModel(Category entity);

    @Mapping(target = "user", ignore = true)
    Category toEntity(CategoryModel model);

    CategoryResponse toDto(CategoryModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    void updateModel(CategoryRequest request, @MappingTarget CategoryModel model);
}