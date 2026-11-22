package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spending.tracker.backend.mapper.UserMapper;
import spending.tracker.backend.model.UserModel;
import spending.tracker.backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDataService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toModel);
    }

    @Transactional
    public UserModel save(UserModel userModel) {
        var user = userMapper.toEntity(userModel);
        return userMapper.toModel(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserModel> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toModel)
                .toList();
    }
}
