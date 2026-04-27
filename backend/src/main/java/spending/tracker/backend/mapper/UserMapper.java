package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.model.UserDto;
import spending.tracker.backend.model.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserModel toModel(User user);

    User toEntity(UserModel userModel);

    UserDto toDto(UserModel userModel);

    UserModel toModel(UserDto userDto);
}
