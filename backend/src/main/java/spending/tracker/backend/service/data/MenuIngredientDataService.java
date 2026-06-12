package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.MenuIngredient;
import spending.tracker.backend.exception.type.MenuIngredientInUseException;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.MenuIngredientMapper;
import spending.tracker.backend.model.MenuIngredientModel;
import spending.tracker.backend.repository.MenuIngredientRepository;
import spending.tracker.backend.repository.MenuMeasureRepository;
import spending.tracker.backend.repository.MenuRecipeIngredientRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuIngredientDataService {

    private final MenuIngredientRepository menuIngredientRepository;
    private final MenuRecipeIngredientRepository menuRecipeIngredientRepository;
    private final MenuMeasureRepository menuMeasureRepository;
    private final MenuIngredientMapper menuIngredientMapper;

    public List<MenuIngredientModel> findAll() {
        return menuIngredientRepository.findAll().stream()
                .map(menuIngredientMapper::toModel)
                .toList();
    }

    public MenuIngredientModel findById(Long id) {
        return menuIngredientRepository.findById(id)
                .map(menuIngredientMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("MenuIngredient", "id", id));
    }

    /**
     * Возвращает JPA-сущность {@link MenuIngredient} по идентификатору
     * или бросает {@link ResourceNotFoundException}. Используется сервисным
     * слоем там, где нужна работа со связями (например, при привязке
     * ингредиента к рецепту).
     */
    public MenuIngredient findEntityByIdOrThrow(Long id) {
        return menuIngredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuIngredient", "id", id));
    }

    public MenuIngredientModel save(MenuIngredientModel model) {
        var entity = menuIngredientMapper.toEntity(model);
        if (entity.getBaseMeasure() == null && model.getBaseMeasureId() != null) {
            var measure = menuMeasureRepository.findById(model.getBaseMeasureId())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuMeasure", "id", model.getBaseMeasureId()));
            entity.setBaseMeasure(measure);
        }
        var saved = menuIngredientRepository.save(entity);
        return menuIngredientMapper.toModel(saved);
    }

    public void deleteById(Long id) {
        menuIngredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuIngredient", "id", id));

        if (menuRecipeIngredientRepository.existsByIngredient_Id(id)) {
            throw new MenuIngredientInUseException(id);
        }

        menuIngredientRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return menuIngredientRepository.existsByName(name);
    }
}
