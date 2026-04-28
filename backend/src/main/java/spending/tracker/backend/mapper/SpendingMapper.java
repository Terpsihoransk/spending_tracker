package spending.tracker.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spending.tracker.backend.dto.SpendingResponse;
import spending.tracker.backend.dto.SpendingRequest;
import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.model.SpendingModel;

@Mapper(componentModel = "spring")
public interface SpendingMapper {

    @Mapping(target = "userEmail", source = "user.email")
    SpendingModel toModel(Spending entity);


    @Mapping(target = "user", ignore = true)
    Spending toEntity(SpendingModel model);

    default User map(String email) {
        if (email == null) {
            return null;
        }
        User user = new User();
        user.setEmail(email);
        return user;
    }

    SpendingResponse toDto(SpendingModel model);

    @Mapping(target = "id", ignore = true)
    SpendingModel toModel(SpendingRequest request, String userEmail);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    void updateModel(SpendingRequest request, @MappingTarget SpendingModel model);
}
