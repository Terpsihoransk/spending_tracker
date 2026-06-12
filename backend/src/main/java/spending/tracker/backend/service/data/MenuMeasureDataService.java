package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.MenuMeasure;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.MenuMeasureMapper;
import spending.tracker.backend.model.MenuMeasureModel;
import spending.tracker.backend.repository.MenuMeasureRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuMeasureDataService {

    private final MenuMeasureRepository menuMeasureRepository;
    private final MenuMeasureMapper menuMeasureMapper;

    public List<MenuMeasureModel> findAll() {
        return menuMeasureRepository.findAll().stream()
                .map(menuMeasureMapper::toModel)
                .toList();
    }

    public MenuMeasureModel findById(Long id) {
        return menuMeasureRepository.findById(id)
                .map(menuMeasureMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("MenuMeasure", "id", id));
    }

    /**
     * Возвращает JPA-сущность {@link MenuMeasure} по идентификатору
     * или бросает {@link ResourceNotFoundException}. Используется сервисным
     * слоем там, где нужна работа со связями (например, при привязке меры
     * к ингредиенту рецепта).
     */
    public MenuMeasure findEntityByIdOrThrow(Long id) {
        return menuMeasureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuMeasure", "id", id));
    }
}
