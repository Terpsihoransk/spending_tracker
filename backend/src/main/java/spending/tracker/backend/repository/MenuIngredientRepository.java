package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.MenuIngredient;

import java.util.Optional;

@Repository
public interface MenuIngredientRepository extends JpaRepository<MenuIngredient, Long> {
    Optional<MenuIngredient> findByName(String name);
    boolean existsByName(String name);
}