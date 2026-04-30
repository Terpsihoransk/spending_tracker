package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spending.tracker.backend.dto.UserRequest;
import spending.tracker.backend.dto.UserResponse;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.model.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toDto(UserModel userModel);

    @Mapping(target = "id", ignore = true)
    //todo если пусто, то кастом, а будем регать
    @Mapping(target = "googleSheetsId", expression = "java(userDto.getGoogleSheetsId() != null && !userDto.getGoogleSheetsId().trim().isEmpty() ? userDto.getGoogleSheetsId() : \"sheet456\")")
    UserModel toModel(UserRequest userDto);

    UserModel toModel(User entity);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    User toEntity(UserModel userModel);
}
