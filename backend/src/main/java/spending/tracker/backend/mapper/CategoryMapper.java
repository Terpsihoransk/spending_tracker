package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spending.tracker.backend.dto.CategoryRequest;
import spending.tracker.backend.dto.CategoryResponse;
import spending.tracker.backend.entity.Category;
import spending.tracker.backend.model.CategoryModel;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "userEmail", source = "user.email")
    CategoryModel toModel(Category entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryModel model);

    CategoryResponse toDto(CategoryModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryRequest request);
}