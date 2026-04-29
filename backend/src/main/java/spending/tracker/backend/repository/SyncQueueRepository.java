package spending.tracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spending.tracker.backend.entity.SyncQueue;
import spending.tracker.backend.entity.SyncQueue.SyncStatus;

import java.util.List;

@Repository
public interface SyncQueueRepository extends JpaRepository<SyncQueue, Long> {

    List<SyncQueue> findByStatus(SyncStatus status);

    @Query("SELECT sq FROM SyncQueue sq WHERE sq.status = :status ORDER BY sq.createdAt ASC")
    List<SyncQueue> findPendingOrderedByCreatedAt(SyncStatus status);

    @Query("SELECT COUNT(sq) FROM SyncQueue sq WHERE sq.status = :status")
    long countByStatus(SyncStatus status);
}