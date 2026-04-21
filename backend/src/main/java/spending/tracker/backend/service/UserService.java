package spending.tracker.backend.service;

import spending.tracker.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean userExists(String email) {
        return userRepository.existsById(email);
    }

}