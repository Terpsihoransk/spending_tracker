package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spending.tracker.backend.dto.UserResponse;
import spending.tracker.backend.dto.UserRequest;
import spending.tracker.backend.model.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toDto(UserModel userModel);

    @Mapping(target = "id", ignore = true)
    UserModel toModel(UserRequest userDto);
}
