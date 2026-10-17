package spending.tracker.backend.repository;

import spending.tracker.backend.entity.Spending;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpendingRepository extends JpaRepository<Spending, Long> {

    @EntityGraph(attributePaths = {"user", "category", "subCategory"})
    @Query("SELECT s FROM Spending s WHERE s.user.id = :userId")
    List<Spending> findByUser_IdWithAssociations(@Param("userId") Long userId);

    // Явная загрузка всех ассоциаций, которые читает SpendingMapper#toModel,
    // чтобы маппинг не падал с LazyInitializationException при open-in-view=false.
    @EntityGraph(attributePaths = {"user", "category", "subCategory"})
    Optional<Spending> findById(Long id);

    boolean existsByCategory_Id(Long categoryId);

    boolean existsBySubCategory_Id(Long subCategoryId);

}