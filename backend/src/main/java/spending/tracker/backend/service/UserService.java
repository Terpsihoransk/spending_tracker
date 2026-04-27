package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDataService userDataService;

    public boolean userExists(String email) {
        return userDataService.findByEmail(email).isPresent();
    }

}
