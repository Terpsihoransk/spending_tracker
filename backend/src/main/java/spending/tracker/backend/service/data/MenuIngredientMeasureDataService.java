package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.repository.MenuIngredientMeasureRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuIngredientMeasureDataService {

    private final MenuIngredientMeasureRepository menuIngredientMeasureRepository;

    public Optional<spending.tracker.backend.entity.MenuIngredientMeasure> findCoefficient(Long ingredientId, Long measureId) {
        return menuIngredientMeasureRepository.findByIngredient_IdAndMeasure_Id(ingredientId, measureId);
    }
}
