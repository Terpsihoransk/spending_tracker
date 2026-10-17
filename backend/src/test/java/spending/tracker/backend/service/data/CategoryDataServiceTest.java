package spending.tracker.backend.service.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import spending.tracker.backend.util.TestDataUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты, проверяющие отсутствие LazyInitializationException
 * при маппинге Category -> CategoryModel в сценарии {@code spring.jpa.open-in-view=false}.
 *
 * <p>Класс намеренно НЕ помечен {@code @Transactional}: транзакцией управляет
 * {@link CategoryDataService}, что соответствует реальному HTTP-запросу.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
@TestPropertySource(properties = {
        "spring.jpa.open-in-view=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration"
})
public class CategoryDataServiceTest {

    @Autowired
    private CategoryDataService categoryDataService;

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    @DisplayName("findAllByUserId: userEmail заполнен, LazyInitializationException не выбрасывается")
    void findAllByUserId_mapsUser_withoutLazyInitializationException() {
        var user = testDataUtils.createUser("lazy-category-list@example.com", "sheet-cat-list");
        testDataUtils.createCategory(user, "Groceries");

        var models = categoryDataService.findAllByUserId(user.getId());

        assertThat(models).hasSize(1);
        assertThat(models.get(0).getName()).isEqualTo("Groceries");
        assertThat(models.get(0).getUserEmail()).isEqualTo("lazy-category-list@example.com");
    }

    @Test
    @DisplayName("findByIdAndUserId: userEmail заполнен, LazyInitializationException не выбрасывается")
    void findByIdAndUserId_mapsUser_withoutLazyInitializationException() {
        var user = testDataUtils.createUser("lazy-category-by-id@example.com", "sheet-cat-id");
        var category = testDataUtils.createCategory(user, "Entertainment");

        var model = categoryDataService.findByIdAndUserId(category.getId(), user.getId());

        assertThat(model.getName()).isEqualTo("Entertainment");
        assertThat(model.getUserEmail()).isEqualTo("lazy-category-by-id@example.com");
    }
}
