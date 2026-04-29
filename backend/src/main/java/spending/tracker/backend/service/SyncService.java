package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.SyncQueue;
import spending.tracker.backend.entity.SyncQueue.EntityType;
import spending.tracker.backend.entity.SyncQueue.Operation;
import spending.tracker.backend.entity.SyncQueue.SyncStatus;
import spending.tracker.backend.model.SpendingModel;
import spending.tracker.backend.repository.SyncQueueRepository;
import spending.tracker.backend.repository.SpendingRepository;
import spending.tracker.backend.service.data.UserDataService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final SyncQueueRepository syncQueueRepository;
    private final SpendingRepository spendingRepository;
    private final GoogleSheetsService googleSheetsService;
    private final UserDataService userDataService;

    private static final int MAX_RETRIES = 3;

    public void queueSync(EntityType entityType, Long entityId, Operation operation) {
        SyncQueue queueItem = new SyncQueue();
        queueItem.setEntityType(entityType);
        queueItem.setEntityId(entityId);
        queueItem.setOperation(operation);
        queueItem.setStatus(SyncStatus.PENDING);
        
        syncQueueRepository.save(queueItem);
        log.info("Queued sync: {} {} {}", entityType, entityId, operation);
    }

    @Async
    public void processQueue() {
        List<SyncQueue> pendingItems = syncQueueRepository.findPendingOrderedByCreatedAt(SyncStatus.PENDING);
        
        for (SyncQueue item : pendingItems) {
            processQueueItem(item);
        }
    }

    private void processQueueItem(SyncQueue item) {
        item.setStatus(SyncStatus.IN_PROGRESS);
        syncQueueRepository.save(item);

        try {
            switch (item.getEntityType()) {
                case SPENDING -> processSpendingSync(item);
                case CATEGORY -> processCategorySync(item);
                case SUBCATEGORY -> processSubCategorySync(item);
            }
            
            item.setStatus(SyncStatus.COMPLETED);
            syncQueueRepository.save(item);
            log.info("Sync completed: {} {} {}", item.getEntityType(), item.getEntityId(), item.getOperation());
            
        } catch (Exception e) {
            handleSyncError(item, e);
        }
    }

    private void processSpendingSync(SyncQueue item) throws IOException {
        Optional<SpendingModel> spendingOpt = spendingRepository.findById(item.getEntityId())
                .map(s -> {
                    SpendingModel model = new SpendingModel();
                    model.setId(s.getId());
                    model.setAmount(s.getAmount());
                    model.setDate(s.getDate());
                    model.setDescription(s.getDescription());
                    model.setUser(s.getUser());
                    model.setCategoryName(s.getCategory() != null ? s.getCategory().getName() : null);
                    model.setSubCategoryName(s.getSubCategory() != null ? s.getSubCategory().getName() : null);
                    return model;
                });

        if (spendingOpt.isEmpty()) {
            log.warn("Spending {} not found, removing from queue", item.getEntityId());
            return;
        }

        SpendingModel spending = spendingOpt.get();

        switch (item.getOperation()) {
            case CREATE -> googleSheetsService.appendSpending(spending);
            case UPDATE -> googleSheetsService.updateSpending(spending);
            case DELETE -> googleSheetsService.deleteSpending(spending.getUser().getGoogleSheetsId(), spending.getUser().getGoogleAccessToken(), spending.getId());
        }
    }

    private void processCategorySync(SyncQueue item) {
        log.info("Category sync not implemented yet for item {}", item.getId());
    }

    private void processSubCategorySync(SyncQueue item) {
        log.info("SubCategory sync not implemented yet for item {}", item.getId());
    }

    private void handleSyncError(SyncQueue item, Exception e) {
        item.incrementRetryCount();
        item.setErrorMessage(e.getMessage());

        if (item.canRetry(MAX_RETRIES)) {
            item.setStatus(SyncStatus.PENDING);
            log.warn("Sync failed for {} {}, retry {}/{}", 
                    item.getEntityType(), item.getEntityId(), item.getRetryCount(), MAX_RETRIES);
        } else {
            item.setStatus(SyncStatus.FAILED);
            log.error("Sync permanently failed for {} {} after {} retries", 
                    item.getEntityType(), item.getEntityId(), item.getRetryCount());
        }

        syncQueueRepository.save(item);
    }

    public SyncStatusSummary getSyncStatus() {
        long pending = syncQueueRepository.countByStatus(SyncStatus.PENDING);
        long inProgress = syncQueueRepository.countByStatus(SyncStatus.IN_PROGRESS);
        long completed = syncQueueRepository.countByStatus(SyncStatus.COMPLETED);
        long failed = syncQueueRepository.countByStatus(SyncStatus.FAILED);

        return new SyncStatusSummary(pending, inProgress, completed, failed);
    }

    public record SyncStatusSummary(long pending, long inProgress, long completed, long failed) {}
}