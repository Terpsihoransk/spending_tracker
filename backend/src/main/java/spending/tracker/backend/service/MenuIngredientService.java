package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.dto.MenuIngredientRequest;
import spending.tracker.backend.dto.MenuIngredientResponse;
import spending.tracker.backend.exception.type.DuplicateMenuIngredientException;
import spending.tracker.backend.mapper.MenuIngredientMapper;
import spending.tracker.backend.model.MenuIngredientModel;
import spending.tracker.backend.service.data.MenuIngredientDataService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuIngredientService {

    private final MenuIngredientDataService menuIngredientDataService;
    private final MenuIngredientMapper menuIngredientMapper;

    public List<MenuIngredientResponse> getAllIngredients() {
        return menuIngredientDataService.findAll().stream()
                .map(menuIngredientMapper::toDto)
                .toList();
    }

    public MenuIngredientResponse getIngredientById(Long id) {
        var model = menuIngredientDataService.findById(id);
        return menuIngredientMapper.toDto(model);
    }

    public MenuIngredientResponse createIngredient(MenuIngredientRequest request) {
        if (menuIngredientDataService.existsByName(request.getName())) {
            throw new DuplicateMenuIngredientException(request.getName());
        }

        var model = new MenuIngredientModel();
        model.setName(request.getName());
        model.setBaseMeasureId(request.getBaseMeasureId());

        var savedModel = menuIngredientDataService.save(model);
        return menuIngredientMapper.toDto(savedModel);
    }

    public void deleteIngredient(Long id) {
        menuIngredientDataService.deleteById(id);
    }
}