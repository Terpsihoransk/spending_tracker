package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spending.tracker.backend.dto.MenuShoppingListItemResponse;
import spending.tracker.backend.dto.MenuShoppingListResponse;
import spending.tracker.backend.entity.MenuShoppingList;
import spending.tracker.backend.service.data.MenuIngredientMeasureDataService;
import spending.tracker.backend.service.data.MenuRecipeDataService;
import spending.tracker.backend.service.data.MenuShoppingListDataService;
import spending.tracker.backend.service.data.UserDataService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuShoppingListService {

    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static final String MEASURE_GRAM = "GRAM";
    private static final String MEASURE_ML = "ML";
    private static final String MEASURE_PIECE = "PIECE";

    private final MenuShoppingListDataService menuShoppingListDataService;
    private final MenuRecipeDataService menuRecipeDataService;
    private final UserDataService userDataService;
    private final MenuIngredientMeasureDataService menuIngredientMeasureDataService;

    @Transactional(readOnly = true)
    public MenuShoppingListResponse getShoppingList(String userEmail) {
        var shoppingListEntries = menuShoppingListDataService.findAllByUserEmail(userEmail);

        List<Long> recipeIds = shoppingListEntries.stream()
                .map(entry -> entry.getRecipe().getId())
                .toList();

        if (shoppingListEntries.isEmpty()) {
            var response = new MenuShoppingListResponse();
            response.setRecipeIds(recipeIds);
            return response;
        }

        // Группировка: ingredientId -> суммарное количество в базовых единицах
        Map<Long, BigDecimal> totals = new HashMap<>();
        // Хранение информации об ингредиентах для последующего маппинга
        Map<Long, String> ingredientNames = new HashMap<>();
        Map<Long, String> baseMeasureCodes = new HashMap<>();

        for (var entry : shoppingListEntries) {
            var recipe = entry.getRecipe();
            for (var recipeIng : recipe.getIngredients()) {
                var ingredient = recipeIng.getIngredient();
                var measure = recipeIng.getMeasure();
                var amount = recipeIng.getAmount();

                // Находим коэффициент конвертации через дата-сервис
                var coeffOpt = menuIngredientMeasureDataService
                        .findCoefficient(ingredient.getId(), measure.getId());
                BigDecimal coefficient = coeffOpt
                        .map(spending.tracker.backend.entity.MenuIngredientMeasure::getBaseUnitCoefficient)
                        .orElse(BigDecimal.ONE);

                BigDecimal baseUnits = amount.multiply(coefficient);

                totals.merge(ingredient.getId(), baseUnits, BigDecimal::add);
                ingredientNames.put(ingredient.getId(), ingredient.getName());
                baseMeasureCodes.put(ingredient.getId(), ingredient.getBaseMeasure().getCode());
            }
        }

        List<MenuShoppingListItemResponse> items = new ArrayList<>();
        for (var entry : totals.entrySet()) {
            Long ingredientId = entry.getKey();
            BigDecimal totalBaseUnits = entry.getValue();
            String baseMeasureCode = baseMeasureCodes.get(ingredientId);
            String ingredientName = ingredientNames.get(ingredientId);

            String displayValue = formatDisplayValue(totalBaseUnits, baseMeasureCode);

            var item = new MenuShoppingListItemResponse();
            item.setIngredientId(ingredientId);
            item.setIngredientName(ingredientName);
            item.setTotalBaseUnits(totalBaseUnits.setScale(2, RoundingMode.HALF_UP));
            item.setBaseMeasureCode(baseMeasureCode);
            item.setDisplayValue(displayValue);

            items.add(item);
        }

        var response = new MenuShoppingListResponse();
        response.setItems(items);
        response.setRecipeIds(recipeIds);
        return response;
    }

    @Transactional
    public void addRecipes(String userEmail, List<Long> recipeIds) {
        var user = userDataService.findEntityByEmailOrThrow(userEmail);

        for (Long recipeId : recipeIds) {
            var recipe = menuRecipeDataService.findEntityByIdAndUserEmail(recipeId, userEmail);

            var existing = menuShoppingListDataService.findByUserEmailAndRecipeId(userEmail, recipeId);
            if (existing.isEmpty()) {
                var entry = new MenuShoppingList();
                entry.setUser(user);
                entry.setRecipe(recipe);
                menuShoppingListDataService.save(entry);
            }
        }
    }

    @Transactional
    public void addRecipe(String userEmail, Long recipeId) {
        addRecipes(userEmail, List.of(recipeId));
    }

    @Transactional
    public void removeRecipe(String userEmail, Long recipeId) {
        menuShoppingListDataService.deleteByUserEmailAndRecipeId(userEmail, recipeId);
    }

    private String formatDisplayValue(BigDecimal amount, String baseMeasureCode) {
        if (MEASURE_GRAM.equals(baseMeasureCode)) {
            if (amount.compareTo(THOUSAND) >= 0) {
                return toBaseMajorUnit(amount).toPlainString() + " кг";
            } else {
                return amount.setScale(0, RoundingMode.HALF_UP).toPlainString() + " гр";
            }
        } else if (MEASURE_ML.equals(baseMeasureCode)) {
            if (amount.compareTo(THOUSAND) >= 0) {
                return toBaseMajorUnit(amount).toPlainString() + " л";
            } else {
                return amount.setScale(0, RoundingMode.HALF_UP).toPlainString() + " мл";
            }
        } else if (MEASURE_PIECE.equals(baseMeasureCode)) {
            return amount.stripTrailingZeros().toPlainString() + " шт";
        } else {
            return amount.stripTrailingZeros().toPlainString() + " " + baseMeasureCode.toLowerCase();
        }
    }

    /**
     * Переводит значение из младших единиц (гр/мл) в старшие (кг/л) с округлением
     * до 2 знаков и удалением trailing zeros.
     */
    private static BigDecimal toBaseMajorUnit(BigDecimal amount) {
        return amount.divide(THOUSAND, 2, RoundingMode.HALF_UP).stripTrailingZeros();
    }
}
