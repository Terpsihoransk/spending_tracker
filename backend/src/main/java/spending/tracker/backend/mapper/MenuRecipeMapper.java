package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import spending.tracker.backend.dto.MenuRecipeResponse;
import spending.tracker.backend.entity.MenuRecipe;
import spending.tracker.backend.entity.MenuRecipeIngredient;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.model.MenuRecipeIngredientModel;
import spending.tracker.backend.model.MenuRecipeModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuRecipeMapper {

    @Mapping(target = "userEmails", source = "users", qualifiedByName = "mapUsersToEmails")
    @Mapping(target = "ingredients", source = "ingredients")
    MenuRecipeModel toModel(MenuRecipe entity);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "ingredientName", source = "ingredient.name")
    @Mapping(target = "measureId", source = "measure.id")
    @Mapping(target = "measureCode", source = "measure.code")
    MenuRecipeIngredientModel toModel(MenuRecipeIngredient entity);

    MenuRecipeResponse toDto(MenuRecipeModel model);

    @Named("mapUsersToEmails")
    default List<String> mapUsersToEmails(List<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(User::getEmail)
                .toList();
    }
}