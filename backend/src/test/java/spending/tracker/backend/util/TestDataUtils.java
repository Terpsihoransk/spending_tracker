package spending.tracker.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.repository.SpendingRepository;
import spending.tracker.backend.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TestDataUtils {

    private final UserRepository userRepository;
    private final SpendingRepository spendingRepository;

    @Autowired
    public TestDataUtils(UserRepository userRepository, SpendingRepository spendingRepository) {
        this.userRepository = userRepository;
        this.spendingRepository = spendingRepository;
    }

    public User createUser(String email, String googleSheetsId) {
        User user = new User();
        user.setEmail(email);
        user.setGoogleSheetsId(googleSheetsId);
        return userRepository.save(user);
    }

    public Spending createSpending(User user, BigDecimal amount, String category, String description) {
        Spending spending = new Spending();
        spending.setUser(user);
        spending.setAmount(amount);
        spending.setCategory(category);
        spending.setDescription(description);
        spending.setDate(LocalDate.now());
        return spendingRepository.save(spending);
    }
}