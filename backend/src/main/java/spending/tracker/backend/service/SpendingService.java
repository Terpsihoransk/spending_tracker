package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.dto.SpendingRequest;
import spending.tracker.backend.dto.SpendingResponse;
import spending.tracker.backend.entity.SyncQueue.EntityType;
import spending.tracker.backend.entity.SyncQueue.Operation;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.SpendingMapper;
import spending.tracker.backend.service.data.SpendingDataService;
import spending.tracker.backend.service.data.UserDataService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpendingService {

    private final SpendingDataService spendingDataService;
    private final SpendingMapper spendingMapper;
    private final UserDataService userDataService;
    private final SyncService syncService;

    public List<SpendingResponse> getAllSpending(String userEmail) {
        return userDataService.findByEmail(userEmail)
                .map(user -> spendingDataService.findAllByUserId(user.getId()))
                .orElse(List.of())
                .stream()
                .map(spendingMapper::toDto)
                .toList();
    }

    public SpendingResponse getSpendingById(Long id, String userEmail) {
        var model = spendingDataService.findById(id);
        model.setUserEmail(userEmail);
        return spendingMapper.toDto(model);
    }

    public SpendingResponse createSpending(SpendingRequest spendingRequest, String userEmail) {
        var userModel = userDataService.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        var model = spendingMapper.toModel(spendingRequest, userEmail);
        model.setDate(LocalDate.now());
        var savedModel = spendingDataService.save(model, userModel.getId());
        
        try {
            syncService.queueSync(EntityType.SPENDING, savedModel.getId(), Operation.CREATE);
        } catch (Exception e) {
            log.error("Failed to queue sync for spending {}: {}", savedModel.getId(), e.getMessage());
        }
        
        return spendingMapper.toDto(savedModel);
    }

    public SpendingResponse updateSpending(Long id, SpendingRequest spendingRequest, String userEmail) {
        var existingModel = spendingDataService.findById(id);
        spendingMapper.updateModel(spendingRequest, existingModel, userEmail);
        existingModel.setUserEmail(userEmail);
        existingModel.setDate(LocalDate.now());
        var updatedModel = spendingDataService.updateSpending(existingModel);
        
        try {
            syncService.queueSync(EntityType.SPENDING, updatedModel.getId(), Operation.UPDATE);
        } catch (Exception e) {
            log.error("Failed to queue sync for spending {}: {}", updatedModel.getId(), e.getMessage());
        }
        
        return spendingMapper.toDto(updatedModel);
    }

    public void deleteSpending(Long id, String userEmail) {
        var spending = spendingDataService.findById(id);
        if (!spending.getUserEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Spending", "id", id.toString());
        }
        
        try {
            syncService.queueSync(EntityType.SPENDING, id, Operation.DELETE);
        } catch (Exception e) {
            log.error("Failed to queue sync for deleted spending {}: {}", id, e.getMessage());
        }
        
        spendingDataService.deleteById(id);
    }
}
