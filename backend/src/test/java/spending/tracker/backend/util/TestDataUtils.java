package spending.tracker.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spending.tracker.backend.entity.Category;
import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.repository.CategoryRepository;
import spending.tracker.backend.repository.SpendingRepository;
import spending.tracker.backend.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TestDataUtils {

    private final UserRepository userRepository;
    private final SpendingRepository spendingRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TestDataUtils(UserRepository userRepository, SpendingRepository spendingRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.spendingRepository = spendingRepository;
        this.categoryRepository = categoryRepository;
    }

    public User createUser(String email, String googleSheetsId) {
        User user = new User();
        user.setEmail(email);
        user.setGoogleSheetsId(googleSheetsId);
        return userRepository.save(user);
    }

    public Category createCategory(User user, String name) {
        Category category = new Category();
        category.setUser(user);
        category.setName(name);
        return categoryRepository.save(category);
    }

    public Spending createSpending(User user, Category category, BigDecimal amount, String description) {
        Spending spending = new Spending();
        spending.setUser(user);
        spending.setCategory(category);
        spending.setAmount(amount);
        spending.setDescription(description);
        spending.setDate(LocalDate.now());
        return spendingRepository.save(spending);
    }
}