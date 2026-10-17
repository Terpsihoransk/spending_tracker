package spending.tracker.backend.repository;

import spending.tracker.backend.entity.Spending;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpendingRepository extends JpaRepository<Spending, Long> {

    @EntityGraph(attributePaths = {"user", "category"})
    @Query("SELECT s FROM Spending s WHERE s.user.id = :userId")
    List<Spending> findByUser_IdWithUserAndCategory(@Param("userId") Long userId);

    boolean existsByCategory_Id(Long categoryId);

    boolean existsBySubCategory_Id(Long subCategoryId);

}