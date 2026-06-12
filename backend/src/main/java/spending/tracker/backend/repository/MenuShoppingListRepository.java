package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.MenuShoppingList;
import spending.tracker.backend.entity.MenuShoppingListId;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuShoppingListRepository extends JpaRepository<MenuShoppingList, MenuShoppingListId> {

    @Query("SELECT s FROM MenuShoppingList s WHERE s.user.email = :email")
    List<MenuShoppingList> findAllByUserEmail(@Param("email") String email);

    @Query("SELECT s FROM MenuShoppingList s WHERE s.user.email = :email AND s.recipe.id = :recipeId")
    Optional<MenuShoppingList> findByUserEmailAndRecipeId(@Param("email") String email, @Param("recipeId") Long recipeId);

    @Modifying
    @Query("DELETE FROM MenuShoppingList s WHERE s.user.email = :email AND s.recipe.id = :recipeId")
    void deleteByUserEmailAndRecipeId(@Param("email") String email, @Param("recipeId") Long recipeId);
}