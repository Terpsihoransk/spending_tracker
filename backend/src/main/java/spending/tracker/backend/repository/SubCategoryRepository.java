package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.SubCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    Optional<SubCategory> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByCategory_IdAndNameAndUser_Email(Long categoryId, String name, String userEmail);

    boolean existsByCategory_IdAndNameAndIdNot(Long categoryId, String name, Long id);

    List<SubCategory> findAllByCategory_IdAndUser_Email(Long categoryId, String userEmail);
}