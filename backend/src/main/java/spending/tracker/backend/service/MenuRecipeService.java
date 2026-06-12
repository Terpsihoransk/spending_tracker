package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spending.tracker.backend.dto.MenuRecipeIngredientRequest;
import spending.tracker.backend.dto.MenuRecipeRequest;
import spending.tracker.backend.dto.MenuRecipeResponse;
import spending.tracker.backend.entity.MenuRecipe;
import spending.tracker.backend.entity.MenuRecipeIngredient;
import spending.tracker.backend.entity.User;
import spending.tracker.backend.exception.type.DuplicateMenuRecipeException;
import spending.tracker.backend.mapper.MenuRecipeMapper;
import spending.tracker.backend.service.data.MenuIngredientDataService;
import spending.tracker.backend.service.data.MenuMeasureDataService;
import spending.tracker.backend.service.data.MenuRecipeDataService;
import spending.tracker.backend.service.data.UserDataService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuRecipeService {

    private final MenuRecipeDataService menuRecipeDataService;
    private final MenuRecipeMapper menuRecipeMapper;
    private final UserDataService userDataService;
    private final MenuIngredientDataService menuIngredientDataService;
    private final MenuMeasureDataService menuMeasureDataService;

    public List<MenuRecipeResponse> getAllRecipes(String userEmail) {
        return menuRecipeDataService.findAllByUserEmail(userEmail).stream()
                .map(menuRecipeMapper::toDto)
                .toList();
    }

    public MenuRecipeResponse getRecipeById(Long id, String userEmail) {
        var model = menuRecipeDataService.findByIdAndUserEmail(id, userEmail);
        return menuRecipeMapper.toDto(model);
    }

    @Transactional
    public MenuRecipeResponse createRecipe(MenuRecipeRequest request, String userEmail) {
        if (menuRecipeDataService.existsByName(request.getName())) {
            throw new DuplicateMenuRecipeException(request.getName());
        }

        var recipe = new MenuRecipe();
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());

        // Находим пользователей (текущий всегда добавляется)
        Set<String> emails = new HashSet<>(request.getUserEmails());
        emails.add(userEmail);
        recipe.setUsers(resolveUsers(emails));

        // Находим и привязываем ингредиенты
        recipe.setIngredients(buildRecipeIngredients(recipe, request.getIngredients()));

        var savedModel = menuRecipeDataService.save(recipe);
        return menuRecipeMapper.toDto(savedModel);
    }

    @Transactional
    public MenuRecipeResponse updateRecipe(Long id, MenuRecipeRequest request, String userEmail) {
        var recipe = menuRecipeDataService.findEntityByIdAndUserEmail(id, userEmail);

        if (!recipe.getName().equals(request.getName()) && menuRecipeDataService.existsByName(request.getName())) {
            throw new DuplicateMenuRecipeException(request.getName());
        }

        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());

        // Обновляем пользователей
        Set<String> emails = new HashSet<>(request.getUserEmails());
        emails.add(userEmail);
        var users = resolveUsers(emails);
        recipe.getUsers().clear();
        recipe.getUsers().addAll(users);

        // Обновляем ингредиенты
        var recipeIngredients = buildRecipeIngredients(recipe, request.getIngredients());
        recipe.getIngredients().clear();
        recipe.getIngredients().addAll(recipeIngredients);

        var savedModel = menuRecipeDataService.save(recipe);
        return menuRecipeMapper.toDto(savedModel);
    }

    @Transactional
    public void deleteRecipe(Long id, String userEmail) {
        // Проверяем доступ перед удалением
        menuRecipeDataService.findEntityByIdAndUserEmail(id, userEmail);
        menuRecipeDataService.deleteById(id);
    }

    /**
     * Резолвит email-ы пользователей в JPA-сущности через {@link UserDataService}.
     */
    private List<User> resolveUsers(Set<String> emails) {
        List<User> users = new ArrayList<>();
        for (String email : emails) {
            users.add(userDataService.findEntityByEmailOrThrow(email));
        }
        return users;
    }

    /**
     * Собирает список {@link MenuRecipeIngredient} для рецепта, резолвя
     * ингредиенты и меры через соответствующие дата-сервисы.
     */
    private List<MenuRecipeIngredient> buildRecipeIngredients(
            MenuRecipe recipe,
            List<MenuRecipeIngredientRequest> ingredientRequests) {
        List<MenuRecipeIngredient> recipeIngredients = new ArrayList<>();
        for (var ingReq : ingredientRequests) {
            var ingredient = menuIngredientDataService.findEntityByIdOrThrow(ingReq.getIngredientId());
            var measure = menuMeasureDataService.findEntityByIdOrThrow(ingReq.getMeasureId());

            var recipeIngredient = new MenuRecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setMeasure(measure);
            recipeIngredient.setAmount(ingReq.getAmount());

            recipeIngredients.add(recipeIngredient);
        }
        return recipeIngredients;
    }
}
