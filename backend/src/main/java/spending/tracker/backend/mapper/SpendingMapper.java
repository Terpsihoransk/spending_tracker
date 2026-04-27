package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.model.SpendingDto;
import spending.tracker.backend.model.SpendingModel;

@Mapper(componentModel = "spring")
public interface SpendingMapper {

    @Mapping(target = "userEmail", source = "user.email")
    SpendingModel toModel(Spending entity);


    @Mapping(target = "user", source = "userEmail")
    Spending toEntity(SpendingModel model);

    default User map(String email) {
        if (email == null) {
            return null;
        }
        User user = new User();
        user.setEmail(email);
        return user;
    }

    SpendingDto toDto(SpendingModel model);

    SpendingModel toModel(SpendingDto dto);

    void updateModel(SpendingDto dto, @MappingTarget SpendingModel model);
}
