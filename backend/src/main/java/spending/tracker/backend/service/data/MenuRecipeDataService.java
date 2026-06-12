package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.MenuRecipe;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.MenuRecipeMapper;
import spending.tracker.backend.model.MenuRecipeModel;
import spending.tracker.backend.repository.MenuRecipeRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuRecipeDataService {

    private final MenuRecipeRepository menuRecipeRepository;
    private final MenuRecipeMapper menuRecipeMapper;

    public List<MenuRecipeModel> findAllByUserEmail(String email) {
        return menuRecipeRepository.findAllByUserEmail(email).stream()
                .map(menuRecipeMapper::toModel)
                .toList();
    }

    public MenuRecipeModel findByIdAndUserEmail(Long id, String email) {
        return menuRecipeRepository.findByIdAndUserEmail(id, email)
                .map(menuRecipeMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("MenuRecipe", "id", id));
    }

    public MenuRecipe findEntityByIdAndUserEmail(Long id, String email) {
        return menuRecipeRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("MenuRecipe", "id", id));
    }

    public MenuRecipeModel save(MenuRecipe entity) {
        var saved = menuRecipeRepository.save(entity);
        return menuRecipeMapper.toModel(saved);
    }

    public void deleteById(Long id) {
        menuRecipeRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return menuRecipeRepository.existsByName(name);
    }
}