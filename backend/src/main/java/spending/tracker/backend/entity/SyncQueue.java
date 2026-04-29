package spending.tracker.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "sync_queue")
@Getter
@Setter
public class SyncQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    @Column(nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Operation operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus status = SyncStatus.PENDING;

    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column
    private Instant lastAttemptAt;

    @Column
    private String errorMessage;

    public enum EntityType {
        SPENDING,
        CATEGORY,
        SUBCATEGORY
    }

    public enum Operation {
        CREATE,
        UPDATE,
        DELETE
    }

    public enum SyncStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.lastAttemptAt = Instant.now();
    }

    public boolean canRetry(int maxRetries) {
        return this.retryCount < maxRetries && this.status == SyncStatus.FAILED;
    }
}