package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.UserMapper;
import spending.tracker.backend.model.UserModel;
import spending.tracker.backend.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDataService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toModel);
    }

    /**
     * Возвращает сущность {@link User} по email без маппинга в модель.
     * Используется сервисным слоем там, где требуется работа с JPA-связями.
     */
    public Optional<User> findEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Возвращает сущность {@link User} по email или бросает {@link ResourceNotFoundException}.
     */
    public User findEntityByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Возвращает список сущностей {@link User} по набору email-ов.
     * Используется там, где нужны JPA-сущности (например, для привязки к рецепту).
     */
    public List<User> findAllEntitiesByEmails(Collection<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return List.of();
        }
        return userRepository.findAllByEmailIn(emails);
    }

    public UserModel save(UserModel userModel) {
        var user = userMapper.toEntity(userModel);
        return userMapper.toModel(userRepository.save(user));
    }

    public List<UserModel> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toModel)
                .toList();
    }
}
