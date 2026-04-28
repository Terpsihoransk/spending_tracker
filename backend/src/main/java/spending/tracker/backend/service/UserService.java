package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.dto.UserRequest;
import spending.tracker.backend.dto.UserResponse;
import spending.tracker.backend.mapper.UserMapper;
import spending.tracker.backend.model.UserModel;
import spending.tracker.backend.service.data.UserDataService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDataService userDataService;
    private final UserMapper userMapper;

    public boolean userExists(String email) {
        return userDataService.findByEmail(email).isPresent();
    }

    public UserResponse saveUser(UserRequest userRequest) {
        UserModel userModel = userMapper.toModel(userRequest);
        UserModel savedModel = userDataService.save(userModel);
        return userMapper.toDto(savedModel);
    }

    public List<UserResponse> getAllUsers() {
        return userDataService.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }
}
