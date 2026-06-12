package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.MenuIngredientMeasure;
import spending.tracker.backend.entity.MenuIngredientMeasureId;

import java.util.Optional;

@Repository
public interface MenuIngredientMeasureRepository extends JpaRepository<MenuIngredientMeasure, MenuIngredientMeasureId> {
    Optional<MenuIngredientMeasure> findByIngredient_IdAndMeasure_Id(Long ingredientId, Long measureId);
}