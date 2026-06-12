package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.MenuRecipe;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRecipeRepository extends JpaRepository<MenuRecipe, Long> {

    @Query("SELECT r FROM MenuRecipe r JOIN r.users u WHERE u.email = :email")
    List<MenuRecipe> findAllByUserEmail(@Param("email") String email);

    @Query("SELECT r FROM MenuRecipe r JOIN r.users u WHERE r.id = :id AND u.email = :email")
    Optional<MenuRecipe> findByIdAndUserEmail(@Param("id") Long id, @Param("email") String email);

    boolean existsByName(String name);
}