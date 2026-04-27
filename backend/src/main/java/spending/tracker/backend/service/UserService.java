package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.mapper.UserMapper;
import spending.tracker.backend.model.UserDto;
import spending.tracker.backend.model.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDataService userDataService;
    private final UserMapper userMapper;

    public boolean userExists(String email) {
        return userDataService.findByEmail(email).isPresent();
    }

    public UserDto saveUser(UserDto userDto) {
        UserModel userModel = userMapper.toModel(userDto);
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setGoogleSheetsId(userModel.getGoogleSheetsId());
        User saved = userDataService.save(user);
        UserModel savedModel = new UserModel();
        savedModel.setEmail(saved.getEmail());
        savedModel.setGoogleSheetsId(saved.getGoogleSheetsId());
        return userMapper.toDto(savedModel);
    }
}
