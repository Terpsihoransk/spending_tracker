package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.MenuShoppingList;
import spending.tracker.backend.repository.MenuShoppingListRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuShoppingListDataService {

    private final MenuShoppingListRepository menuShoppingListRepository;

    public List<MenuShoppingList> findAllByUserEmail(String email) {
        return menuShoppingListRepository.findAllByUserEmail(email);
    }

    public Optional<MenuShoppingList> findByUserEmailAndRecipeId(String email, Long recipeId) {
        return menuShoppingListRepository.findByUserEmailAndRecipeId(email, recipeId);
    }

    public MenuShoppingList save(MenuShoppingList entity) {
        return menuShoppingListRepository.save(entity);
    }

    public void deleteByUserEmailAndRecipeId(String email, Long recipeId) {
        menuShoppingListRepository.deleteByUserEmailAndRecipeId(email, recipeId);
    }
}