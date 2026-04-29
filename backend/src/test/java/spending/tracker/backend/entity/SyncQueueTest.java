package spending.tracker.backend.entity;

import org.junit.jupiter.api.Test;
import spending.tracker.backend.entity.SyncQueue.EntityType;
import spending.tracker.backend.entity.SyncQueue.Operation;
import spending.tracker.backend.entity.SyncQueue.SyncStatus;

import static org.junit.jupiter.api.Assertions.*;

class SyncQueueTest {

    @Test
    void incrementRetryCount_ShouldIncreaseCount() {
        SyncQueue queue = new SyncQueue();
        queue.setRetryCount(0);
        
        queue.incrementRetryCount();
        
        assertEquals(1, queue.getRetryCount());
        assertNotNull(queue.getLastAttemptAt());
    }

    @Test
    void canRetry_ShouldReturnTrue_WhenUnderMaxRetries() {
        SyncQueue queue = new SyncQueue();
        queue.setRetryCount(2);
        queue.setStatus(SyncStatus.FAILED);
        
        assertTrue(queue.canRetry(3));
    }

    @Test
    void canRetry_ShouldReturnFalse_WhenAtMaxRetries() {
        SyncQueue queue = new SyncQueue();
        queue.setRetryCount(3);
        queue.setStatus(SyncStatus.FAILED);
        
        assertFalse(queue.canRetry(3));
    }

    @Test
    void canRetry_ShouldReturnFalse_WhenNotFailed() {
        SyncQueue queue = new SyncQueue();
        queue.setRetryCount(1);
        queue.setStatus(SyncStatus.PENDING);
        
        assertFalse(queue.canRetry(3));
    }

    @Test
    void defaultValues_ShouldBeSet() {
        SyncQueue queue = new SyncQueue();
        
        assertEquals(SyncStatus.PENDING, queue.getStatus());
        assertEquals(0, queue.getRetryCount());
        assertNotNull(queue.getCreatedAt());
    }

    @Test
    void enums_ShouldHaveCorrectValues() {
        assertEquals(3, EntityType.values().length);
        assertEquals(3, Operation.values().length);
        assertEquals(4, SyncStatus.values().length);
    }
}