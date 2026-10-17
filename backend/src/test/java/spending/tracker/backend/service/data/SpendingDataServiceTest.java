package spending.tracker.backend.service.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import spending.tracker.backend.util.TestDataUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты, проверяющие отсутствие LazyInitializationException
 * при маппинге Spending -> SpendingModel в сценарии {@code spring.jpa.open-in-view=false}.
 *
 * <p>Класс намеренно НЕ помечен {@code @Transactional}: в противном случае сессия Hibernate
 * была бы открыта на всё время теста и замаскировала бы проблему "no session" во время
 * маппинга lazy-ассоциаций (SubCategory). Так тест воспроизводит поведение реального
 * HTTP-запроса, где транзакцией управляет только {@link SpendingDataService}.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
@TestPropertySource(properties = {
        "spring.jpa.open-in-view=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration"
})
public class SpendingDataServiceTest {

    @Autowired
    private SpendingDataService spendingDataService;

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    @DisplayName("findAllByUserId: subcategoryName заполнен, LazyInitializationException не выбрасывается")
    void findAllByUserId_mapsSubCategory_withoutLazyInitializationException() {
        var user = testDataUtils.createUser("lazy-spending-list@example.com", "sheet-lazy-list");
        var category = testDataUtils.createCategory(user, "Food");
        var subCategory = testDataUtils.createSubCategory(user, category, "Restaurants");
        testDataUtils.createSpendingWithSubCategory(user, category, subCategory, new BigDecimal("99.90"), "Lunch");

        var models = spendingDataService.findAllByUserId(user.getId());

        assertThat(models).hasSize(1);
        var model = models.get(0);
        assertThat(model.getUserEmail()).isEqualTo("lazy-spending-list@example.com");
        assertThat(model.getCategoryName()).isEqualTo("Food");
        assertThat(model.getSubcategoryName()).isEqualTo("Restaurants");
    }

    @Test
    @DisplayName("findById: subcategoryName заполнен, LazyInitializationException не выбрасывается")
    void findById_mapsSubCategory_withoutLazyInitializationException() {
        var user = testDataUtils.createUser("lazy-spending-by-id@example.com", "sheet-lazy-id");
        var category = testDataUtils.createCategory(user, "Auto");
        var subCategory = testDataUtils.createSubCategory(user, category, "Fuel");
        var spending = testDataUtils.createSpendingWithSubCategory(
                user, category, subCategory, new BigDecimal("42.00"), "Refuel");

        var model = spendingDataService.findById(spending.getId());

        assertThat(model.getCategoryName()).isEqualTo("Auto");
        assertThat(model.getSubcategoryName()).isEqualTo("Fuel");
        assertThat(model.getUserEmail()).isEqualTo("lazy-spending-by-id@example.com");
    }
}
