package spending.tracker.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spending.tracker.backend.entity.SyncQueue;
import spending.tracker.backend.entity.SyncQueue.EntityType;
import spending.tracker.backend.entity.SyncQueue.Operation;
import spending.tracker.backend.entity.SyncQueue.SyncStatus;
import spending.tracker.backend.repository.SyncQueueRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    @Mock
    private SyncQueueRepository syncQueueRepository;

    @InjectMocks
    private SyncService syncService;

    private SyncQueue pendingQueueItem;

    @BeforeEach
    void setUp() {
        pendingQueueItem = new SyncQueue();
        pendingQueueItem.setId(1L);
        pendingQueueItem.setEntityType(EntityType.SPENDING);
        pendingQueueItem.setEntityId(100L);
        pendingQueueItem.setOperation(Operation.CREATE);
        pendingQueueItem.setStatus(SyncStatus.PENDING);
    }

    @Test
    void queueSync_ShouldCreatePendingQueueItem() {
        when(syncQueueRepository.save(any(SyncQueue.class))).thenReturn(pendingQueueItem);

        syncService.queueSync(EntityType.SPENDING, 100L, Operation.CREATE);

        verify(syncQueueRepository, times(1)).save(argThat(queue -> 
            queue.getEntityType() == EntityType.SPENDING &&
            queue.getEntityId() == 100L &&
            queue.getOperation() == Operation.CREATE &&
            queue.getStatus() == SyncStatus.PENDING
        ));
    }

    @Test
    void getSyncStatus_ShouldReturnCorrectCounts() {
        when(syncQueueRepository.countByStatus(SyncStatus.PENDING)).thenReturn(5L);
        when(syncQueueRepository.countByStatus(SyncStatus.IN_PROGRESS)).thenReturn(2L);
        when(syncQueueRepository.countByStatus(SyncStatus.COMPLETED)).thenReturn(10L);
        when(syncQueueRepository.countByStatus(SyncStatus.FAILED)).thenReturn(1L);

        SyncService.SyncStatusSummary status = syncService.getSyncStatus();

        assertEquals(5L, status.pending());
        assertEquals(2L, status.inProgress());
        assertEquals(10L, status.completed());
        assertEquals(1L, status.failed());
    }

    @Test
    void queueSync_ForDeleteOperation_ShouldCreateCorrectQueueItem() {
        when(syncQueueRepository.save(any(SyncQueue.class))).thenReturn(pendingQueueItem);

        syncService.queueSync(EntityType.SPENDING, 200L, Operation.DELETE);

        verify(syncQueueRepository, times(1)).save(argThat(queue -> 
            queue.getOperation() == Operation.DELETE
        ));
    }

    @Test
    void queueSync_ForCategory_ShouldCreateCorrectQueueItem() {
        when(syncQueueRepository.save(any(SyncQueue.class))).thenReturn(pendingQueueItem);

        syncService.queueSync(EntityType.CATEGORY, 50L, Operation.UPDATE);

        verify(syncQueueRepository, times(1)).save(argThat(queue -> 
            queue.getEntityType() == EntityType.CATEGORY &&
            queue.getEntityId() == 50L &&
            queue.getOperation() == Operation.UPDATE
        ));
    }
}