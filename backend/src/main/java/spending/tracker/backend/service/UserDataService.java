package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDataService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
