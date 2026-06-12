package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.MenuMeasure;

import java.util.Optional;

@Repository
public interface MenuMeasureRepository extends JpaRepository<MenuMeasure, Long> {
    Optional<MenuMeasure> findByCode(String code);
}