package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.MenuRecipeIngredient;
import spending.tracker.backend.entity.MenuRecipeIngredientId;

@Repository
public interface MenuRecipeIngredientRepository extends JpaRepository<MenuRecipeIngredient, MenuRecipeIngredientId> {
    boolean existsByIngredient_Id(Long ingredientId);
}