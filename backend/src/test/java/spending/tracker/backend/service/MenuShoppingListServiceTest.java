package spending.tracker.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import spending.tracker.backend.base.BaseUnitTest;
import spending.tracker.backend.entity.MenuIngredient;
import spending.tracker.backend.entity.MenuIngredientMeasure;
import spending.tracker.backend.entity.MenuMeasure;
import spending.tracker.backend.entity.MenuRecipe;
import spending.tracker.backend.entity.MenuRecipeIngredient;
import spending.tracker.backend.entity.MenuShoppingList;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.service.data.MenuIngredientMeasureDataService;
import spending.tracker.backend.service.data.MenuRecipeDataService;
import spending.tracker.backend.service.data.MenuShoppingListDataService;
import spending.tracker.backend.service.data.UserDataService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты для {@link MenuShoppingListService}.
 *
 * <p>Цель — проверить корректность алгоритма конвертации единиц измерения
 * (штуки → граммы, литры → миллилитры, ложки → граммы) и форматирования
 * отображаемого значения (гр → кг, мл → л).</p>
 *
 * <p>Тесты выполняются в полной изоляции от Spring-контекста: все зависимости
 * сервиса подменяются моками.</p>
 */
@DisplayName("MenuShoppingListService — конвертация мер и весов")
class MenuShoppingListServiceTest extends BaseUnitTest {

    @Mock
    private MenuShoppingListDataService menuShoppingListDataService;

    @Mock
    private MenuRecipeDataService menuRecipeDataService;

    @Mock
    private UserDataService userDataService;

    @Mock
    private MenuIngredientMeasureDataService menuIngredientMeasureDataService;

    @InjectMocks
    private MenuShoppingListService service;

    // -----------------------------------------------------------------------------------
    // Пустой список покупок
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен вернуть пустой ответ когда у пользователя нет рецептов в списке покупок")
    void shouldReturnEmptyShoppingListWhenUserHasNoRecipes() {
        when(menuShoppingListDataService.findAllByUserEmail(USER_EMAIL)).thenReturn(List.of());

        var response = service.getShoppingList(USER_EMAIL);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getRecipeIds()).isEmpty();
        verify(menuIngredientMeasureDataService, never()).findCoefficient(any(), any());
    }

    // -----------------------------------------------------------------------------------
    // Конвертация: PIECE → GRAM
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен сконвертировать 1 штуку (150 гр) и 200 гр моркови в суммарные 350 гр")
    void shouldConvertPieceToGramsCarrot() {
        // 1 шт = 150 гр; 200 гр = 200 гр; итого 350 гр
        var gram = measure(1L, "GRAM");
        var piece = measure(2L, "PIECE");
        var carrot = ingredient(10L, "Морковь", gram);

        mockCoefficient(carrot, piece, "150");
        mockCoefficient(carrot, gram, "1");

        var recipe1 = recipe(100L, "Салат", List.of(
                recipeIngredient(carrot, piece, "1")
        ));
        var recipe2 = recipe(200L, "Суп", List.of(
                recipeIngredient(carrot, gram, "200")
        ));

        stubShoppingList(recipe1, recipe2);

        var response = service.getShoppingList(USER_EMAIL);

        assertThat(response.getItems()).hasSize(1);
        var item = response.getItems().getFirst();
        assertThat(item.getIngredientId()).isEqualTo(carrot.getId());
        assertThat(item.getIngredientName()).isEqualTo("Морковь");
        assertThat(item.getBaseMeasureCode()).isEqualTo("GRAM");
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("350.00"));
        assertThat(item.getDisplayValue()).isEqualTo("350 гр");
        assertThat(response.getRecipeIds()).containsExactly(100L, 200L);
    }

    // -----------------------------------------------------------------------------------
    // Конвертация: CUP + LITER → ML
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен сконвертировать 1 стакан (250 мл) и 0.5 л (500 мл) молока в 750 мл")
    void shouldConvertCupAndLiterToMillilitersMilk() {
        var ml = measure(1L, "ML");
        var liter = measure(2L, "LITER");
        var cup = measure(3L, "CUP");
        var milk = ingredient(20L, "Молоко", ml);

        mockCoefficient(milk, ml, "1");
        mockCoefficient(milk, liter, "1000");
        mockCoefficient(milk, cup, "250");

        var recipe1 = recipe(100L, "Каша", List.of(
                recipeIngredient(milk, cup, "1")
        ));
        var recipe2 = recipe(200L, "Омлет", List.of(
                recipeIngredient(milk, liter, "0.5")
        ));

        stubShoppingList(recipe1, recipe2);

        var response = service.getShoppingList(USER_EMAIL);

        assertThat(response.getItems()).hasSize(1);
        var item = response.getItems().getFirst();
        assertThat(item.getIngredientName()).isEqualTo("Молоко");
        assertThat(item.getBaseMeasureCode()).isEqualTo("ML");
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("750.00"));
        assertThat(item.getDisplayValue()).isEqualTo("750 мл");
    }

    // -----------------------------------------------------------------------------------
    // Конвертация: TABLESPOON + TEASPOON → GRAM
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен сконвертировать 1 ст.л. (15 гр) и 1 ч.л. (5 гр) масла в 20 гр")
    void shouldConvertTablespoonAndTeaspoonToGrams() {
        var gram = measure(1L, "GRAM");
        var tablespoon = measure(2L, "TABLESPOON");
        var teaspoon = measure(3L, "TEASPOON");
        var oil = ingredient(30L, "Масло", gram);

        mockCoefficient(oil, gram, "1");
        mockCoefficient(oil, tablespoon, "15");
        mockCoefficient(oil, teaspoon, "5");

        var recipe1 = recipe(100L, "Тесто", List.of(
                recipeIngredient(oil, tablespoon, "1")
        ));
        var recipe2 = recipe(200L, "Крем", List.of(
                recipeIngredient(oil, teaspoon, "1")
        ));

        stubShoppingList(recipe1, recipe2);

        var response = service.getShoppingList(USER_EMAIL);

        assertThat(response.getItems()).hasSize(1);
        var item = response.getItems().getFirst();
        assertThat(item.getIngredientName()).isEqualTo("Масло");
        assertThat(item.getBaseMeasureCode()).isEqualTo("GRAM");
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(item.getDisplayValue()).isEqualTo("20 гр");
    }

    // -----------------------------------------------------------------------------------
    // Граничные случаи форматирования GRAM (гр / кг)
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен отображать ровно 1000 грамм как '1 кг'")
    void shouldDisplayExactly1000GramsAsKilograms() {
        var gram = measure(1L, "GRAM");
        var kg = measure(2L, "KG");
        var flour = ingredient(40L, "Мука", gram);

        mockCoefficient(flour, gram, "1");
        mockCoefficient(flour, kg, "1000");

        var recipe = recipe(100L, "Хлеб", List.of(
                recipeIngredient(flour, kg, "1")
        ));
        stubShoppingList(recipe);

        var response = service.getShoppingList(USER_EMAIL);

        assertThat(response.getItems()).hasSize(1);
        var item = response.getItems().getFirst();
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(item.getDisplayValue()).isEqualTo("1 кг");
    }

    @Test
    @DisplayName("должен отображать 1500 грамм как '1.5 кг'")
    void shouldDisplay1500GramsAsKilograms() {
        var gram = measure(1L, "GRAM");
        var kg = measure(2L, "KG");
        var flour = ingredient(41L, "Мука", gram);

        mockCoefficient(flour, gram, "1");
        mockCoefficient(flour, kg, "1000");

        var recipe1 = recipe(100L, "Хлеб", List.of(
                recipeIngredient(flour, kg, "1")
        ));
        var recipe2 = recipe(200L, "Блины", List.of(
                recipeIngredient(flour, gram, "500")
        ));
        stubShoppingList(recipe1, recipe2);

        var response = service.getShoppingList(USER_EMAIL);

        assertThat(response.getItems()).hasSize(1);
        var item = response.getItems().getFirst();
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(item.getDisplayValue()).isEqualTo("1.5 кг");
    }

    @Test
    @DisplayName("должен отображать 999 грамм как '999 гр' (не переводить в кг)")
    void shouldDisplay999GramsAsGrams() {
        var gram = measure(1L, "GRAM");
        var sugar = ingredient(50L, "Сахар", gram);

        mockCoefficient(sugar, gram, "1");

        var recipe = recipe(100L, "Пирог", List.of(
                recipeIngredient(sugar, gram, "999")
        ));
        stubShoppingList(recipe);

        var response = service.getShoppingList(USER_EMAIL);

        var item = response.getItems().getFirst();
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("999.00"));
        assertThat(item.getDisplayValue()).isEqualTo("999 гр");
    }

    // -----------------------------------------------------------------------------------
    // Граничные случаи форматирования ML (мл / л)
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен отображать 1500 мл как '1.5 л'")
    void shouldDisplay1500MlAsLiters() {
        var ml = measure(1L, "ML");
        var liter = measure(2L, "LITER");
        var water = ingredient(60L, "Вода", ml);

        mockCoefficient(water, ml, "1");
        mockCoefficient(water, liter, "1000");

        var recipe1 = recipe(100L, "Суп", List.of(
                recipeIngredient(water, liter, "1")
        ));
        var recipe2 = recipe(200L, "Чай", List.of(
                recipeIngredient(water, ml, "500")
        ));
        stubShoppingList(recipe1, recipe2);

        var response = service.getShoppingList(USER_EMAIL);

        var item = response.getItems().getFirst();
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(item.getDisplayValue()).isEqualTo("1.5 л");
    }

    @Test
    @DisplayName("должен отображать 999 мл как '999 мл' (не переводить в л)")
    void shouldDisplay999MlAsMilliliters() {
        var ml = measure(1L, "ML");
        var water = ingredient(61L, "Вода", ml);

        mockCoefficient(water, ml, "1");

        var recipe = recipe(100L, "Чай", List.of(
                recipeIngredient(water, ml, "999")
        ));
        stubShoppingList(recipe);

        var response = service.getShoppingList(USER_EMAIL);

        var item = response.getItems().getFirst();
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("999.00"));
        assertThat(item.getDisplayValue()).isEqualTo("999 мл");
    }

    // -----------------------------------------------------------------------------------
    // PIECE как базовая мера
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен отображать ингредиент с базовой мерой PIECE в штуках")
    void shouldDisplayPieceAsBaseMeasure() {
        var piece = measure(1L, "PIECE");
        var egg = ingredient(70L, "Яйцо", piece);

        mockCoefficient(egg, piece, "1");

        var recipe1 = recipe(100L, "Омлет", List.of(
                recipeIngredient(egg, piece, "2")
        ));
        var recipe2 = recipe(200L, "Тесто", List.of(
                recipeIngredient(egg, piece, "3")
        ));
        stubShoppingList(recipe1, recipe2);

        var response = service.getShoppingList(USER_EMAIL);

        var item = response.getItems().getFirst();
        assertThat(item.getIngredientName()).isEqualTo("Яйцо");
        assertThat(item.getBaseMeasureCode()).isEqualTo("PIECE");
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(item.getDisplayValue()).isEqualTo("5 шт");
    }

    // -----------------------------------------------------------------------------------
    // Коэффициент по умолчанию
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен использовать коэффициент 1 если коэффициент для пары не задан в БД")
    void shouldUseDefaultCoefficientOneWhenMissing() {
        var gram = measure(1L, "GRAM");
        var piece = measure(2L, "PIECE");
        var unknownIngredient = ingredient(80L, "Что-то", gram);

        // намеренно не настраиваем mockCoefficient для (unknownIngredient, piece) — будет Optional.empty
        when(menuIngredientMeasureDataService
                .findCoefficient(unknownIngredient.getId(), piece.getId()))
                .thenReturn(java.util.Optional.empty());
        // для базовой GRAM тоже пусто
        when(menuIngredientMeasureDataService
                .findCoefficient(unknownIngredient.getId(), gram.getId()))
                .thenReturn(java.util.Optional.empty());

        var recipe = recipe(100L, "Рецепт", List.of(
                recipeIngredient(unknownIngredient, piece, "3"),
                recipeIngredient(unknownIngredient, gram, "50")
        ));
        stubShoppingList(recipe);

        var response = service.getShoppingList(USER_EMAIL);

        var item = response.getItems().getFirst();
        // 3 * 1 + 50 * 1 = 53
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("53.00"));
        assertThat(item.getBaseMeasureCode()).isEqualTo("GRAM");
        assertThat(item.getDisplayValue()).isEqualTo("53 гр");
    }

    // -----------------------------------------------------------------------------------
    // Группировка нескольких ингредиентов
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен вернуть несколько ингредиентов в списке покупок независимо")
    void shouldReturnMultipleIngredientsSeparately() {
        var gram = measure(1L, "GRAM");
        var ml = measure(2L, "ML");
        var piece = measure(3L, "PIECE");
        var carrot = ingredient(90L, "Морковь", gram);
        var milk = ingredient(91L, "Молоко", ml);
        var egg = ingredient(92L, "Яйцо", piece);

        mockCoefficient(carrot, piece, "150");
        mockCoefficient(carrot, gram, "1");
        mockCoefficient(milk, ml, "1");
        mockCoefficient(milk, piece, "50");
        mockCoefficient(egg, piece, "1");

        var recipe1 = recipe(100L, "Суп", List.of(
                recipeIngredient(carrot, piece, "1"),
                recipeIngredient(milk, ml, "200")
        ));
        var recipe2 = recipe(200L, "Омлет", List.of(
                recipeIngredient(egg, piece, "2"),
                recipeIngredient(milk, piece, "1")
        ));
        stubShoppingList(recipe1, recipe2);

        var response = service.getShoppingList(USER_EMAIL);

        assertThat(response.getItems()).hasSize(3);
        assertThat(response.getItems())
                .extracting(i -> i.getIngredientName() + "|" + i.getBaseMeasureCode() + "|" + i.getDisplayValue())
                .containsExactlyInAnyOrder(
                        "Морковь|GRAM|150 гр",
                        "Молоко|ML|250 мл",
                        "Яйцо|PIECE|2 шт"
                );
    }

    // -----------------------------------------------------------------------------------
    // Сложение одного ингредиента из разных рецептов
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен суммировать один ингредиент из трёх рецептов")
    void shouldSumSameIngredientFromMultipleRecipes() {
        var gram = measure(1L, "GRAM");
        var piece = measure(2L, "PIECE");
        var carrot = ingredient(100L, "Морковь", gram);

        mockCoefficient(carrot, gram, "1");
        mockCoefficient(carrot, piece, "150");

        var recipe1 = recipe(101L, "Рецепт 1", List.of(recipeIngredient(carrot, piece, "1")));   // 150
        var recipe2 = recipe(102L, "Рецепт 2", List.of(recipeIngredient(carrot, gram, "200"))); // 200
        var recipe3 = recipe(103L, "Рецепт 3", List.of(recipeIngredient(carrot, piece, "2")));   // 300
        stubShoppingList(recipe1, recipe2, recipe3);

        var response = service.getShoppingList(USER_EMAIL);

        assertThat(response.getItems()).hasSize(1);
        var item = response.getItems().getFirst();
        assertThat(item.getTotalBaseUnits()).isEqualByComparingTo(new BigDecimal("650.00"));
        assertThat(item.getDisplayValue()).isEqualTo("650 гр");
        assertThat(response.getRecipeIds()).containsExactly(101L, 102L, 103L);
    }

    // -----------------------------------------------------------------------------------
    // Добавление и удаление рецептов
    // -----------------------------------------------------------------------------------

    @Test
    @DisplayName("должен добавить новый рецепт в список покупок когда его ещё нет")
    void shouldAddRecipeToShoppingListWhenAbsent() {
        var user = user(1L);
        var recipe = recipe(500L, "Борщ", List.of());

        when(userDataService.findEntityByEmailOrThrow(USER_EMAIL)).thenReturn(user);
        when(menuRecipeDataService.findEntityByIdAndUserEmail(500L, USER_EMAIL)).thenReturn(recipe);
        when(menuShoppingListDataService.findByUserEmailAndRecipeId(USER_EMAIL, 500L))
                .thenReturn(java.util.Optional.empty());

        service.addRecipe(USER_EMAIL, 500L);

        verify(menuShoppingListDataService, times(1)).save(any(MenuShoppingList.class));
    }

    @Test
    @DisplayName("не должен сохранять рецепт повторно если он уже в списке покупок")
    void shouldNotSaveDuplicateRecipeToShoppingList() {
        var user = user(2L);
        var recipe = recipe(600L, "Каша", List.of());
        var existing = shoppingListEntry(user, recipe);

        when(userDataService.findEntityByEmailOrThrow(USER_EMAIL)).thenReturn(user);
        when(menuRecipeDataService.findEntityByIdAndUserEmail(600L, USER_EMAIL)).thenReturn(recipe);
        when(menuShoppingListDataService.findByUserEmailAndRecipeId(USER_EMAIL, 600L))
                .thenReturn(java.util.Optional.of(existing));

        service.addRecipe(USER_EMAIL, 600L);

        verify(menuShoppingListDataService, never()).save(any());
    }

    @Test
    @DisplayName("должен выбросить ResourceNotFoundException при добавлении рецепта для несуществующего пользователя")
    void shouldThrowWhenUserNotFoundOnAddRecipe() {
        when(userDataService.findEntityByEmailOrThrow(USER_EMAIL))
                .thenThrow(new ResourceNotFoundException("User", "email", USER_EMAIL));

        assertThatThrownBy(() -> service.addRecipe(USER_EMAIL, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("должен удалить рецепт из списка покупок")
    void shouldRemoveRecipeFromShoppingList() {
        service.removeRecipe(USER_EMAIL, 999L);

        verify(menuShoppingListDataService, times(1))
                .deleteByUserEmailAndRecipeId(USER_EMAIL, 999L);
    }

    // -----------------------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------------------

    /**
     * Регистрирует ответ мока {@code menuIngredientMeasureDataService}
     * для пары (ингредиент, мера) с заданным коэффициентом.
     */
    private void mockCoefficient(MenuIngredient ingredient, MenuMeasure measure, String coefficient) {
        var mockEntity = new MenuIngredientMeasure();
        mockEntity.setIngredient(ingredient);
        mockEntity.setMeasure(measure);
        mockEntity.setBaseUnitCoefficient(new BigDecimal(coefficient));

        when(menuIngredientMeasureDataService
                .findCoefficient(ingredient.getId(), measure.getId()))
                .thenReturn(java.util.Optional.of(mockEntity));
    }

    private void stubShoppingList(MenuRecipe... recipes) {
        var user = user(1L);
        var entries = new ArrayList<MenuShoppingList>();
        for (MenuRecipe recipe : recipes) {
            var entry = shoppingListEntry(user, recipe);
            entries.add(entry);
        }
        when(menuShoppingListDataService.findAllByUserEmail(USER_EMAIL)).thenReturn(entries);
    }

    // --- entity factories ---

    private static MenuMeasure measure(Long id, String code) {
        var m = new MenuMeasure();
        m.setId(id);
        m.setCode(code);
        return m;
    }

    private static MenuIngredient ingredient(Long id, String name, MenuMeasure baseMeasure) {
        var i = new MenuIngredient();
        i.setId(id);
        i.setName(name);
        i.setBaseMeasure(baseMeasure);
        return i;
    }

    private static MenuRecipeIngredient recipeIngredient(MenuIngredient ingredient, MenuMeasure measure, String amount) {
        var ri = new MenuRecipeIngredient();
        ri.setIngredient(ingredient);
        ri.setMeasure(measure);
        ri.setAmount(new BigDecimal(amount));
        return ri;
    }

    private static MenuRecipe recipe(Long id, String name, List<MenuRecipeIngredient> ingredients) {
        var r = new MenuRecipe();
        r.setId(id);
        r.setName(name);
        r.setDescription("");
        r.getIngredients().addAll(ingredients);
        // проставляем обратную ссылку recipe, чтобы соответствовать JPA-связи
        for (MenuRecipeIngredient ri : ingredients) {
            ri.setRecipe(r);
        }
        return r;
    }

    private static MenuShoppingList shoppingListEntry(User user, MenuRecipe recipe) {
        var sl = new MenuShoppingList();
        sl.setUser(user);
        sl.setRecipe(recipe);
        return sl;
    }

    private static User user(Long id) {
        var u = new User();
        u.setId(id);
        u.setEmail(USER_EMAIL);
        return u;
    }
}
