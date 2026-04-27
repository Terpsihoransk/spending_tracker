package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import spending.tracker.backend.model.UserDto;
import spending.tracker.backend.model.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(UserModel userModel);

    UserModel toModel(UserDto userDto);
}
