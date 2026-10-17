package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId")
    List<Category> findByUser_IdWithUser(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.user.id = :userId")
    Optional<Category> findByIdAndUser_IdWithUser(@Param("id") Long id, @Param("userId") Long userId);

    boolean existsByUser_IdAndName(Long userId, String name);
}