package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.dto.SpendingRequest;
import spending.tracker.backend.dto.SpendingResponse;
import spending.tracker.backend.exception.ResourceNotFoundException;
import spending.tracker.backend.mapper.SpendingMapper;
import spending.tracker.backend.model.SpendingModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpendingService {

    private final SpendingDataService spendingDataService;
    private final SpendingMapper spendingMapper;
    private final UserDataService userDataService;

    public List<SpendingResponse> getAllSpending(String userEmail) {
        return spendingDataService.findAllByUserEmail(userEmail).stream()
                .map(spendingMapper::toDto)
                .toList();
    }

    public SpendingResponse getSpendingById(Long id) {
        SpendingModel model = spendingDataService.findById(id);
        return spendingMapper.toDto(model);
    }

    public SpendingResponse createSpending(SpendingRequest spendingRequest, String userEmail) {
        userDataService.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        SpendingModel model = spendingMapper.toModel(spendingRequest, userEmail);
        model.setUserEmail(userEmail);
        SpendingModel savedModel = spendingDataService.save(model);
        return spendingMapper.toDto(savedModel);
    }

    public SpendingResponse updateSpending(Long id, SpendingRequest spendingRequest) {
        SpendingModel existingModel = spendingDataService.findById(id);
        spendingMapper.updateModel(spendingRequest, existingModel);
        SpendingModel updatedModel = spendingDataService.save(existingModel);
        return spendingMapper.toDto(updatedModel);
    }

    public void deleteSpending(Long id) {
        spendingDataService.deleteById(id);
    }
}
