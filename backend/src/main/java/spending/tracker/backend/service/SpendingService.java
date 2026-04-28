package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.dto.SpendingRequest;
import spending.tracker.backend.dto.SpendingResponse;
import spending.tracker.backend.exception.ResourceNotFoundException;
import spending.tracker.backend.mapper.SpendingMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpendingService {

    private final SpendingDataService spendingDataService;
    private final SpendingMapper spendingMapper;
    private final UserDataService userDataService;

    public List<SpendingResponse> getAllSpending(String userEmail) {
        return userDataService.findByEmail(userEmail)
                .map(user -> spendingDataService.findAllByUserId(user.getId()))
                .orElse(List.of())
                .stream()
                .map(spendingMapper::toDto)
                .toList();
    }

    public SpendingResponse getSpendingById(Long id) {
        var model = spendingDataService.findById(id);
        return spendingMapper.toDto(model);
    }

    public SpendingResponse createSpending(SpendingRequest spendingRequest, String userEmail) {
        var userModel = userDataService.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        var model = spendingMapper.toModel(spendingRequest, userEmail);
        var savedModel = spendingDataService.save(model, userModel.getId());
        return spendingMapper.toDto(savedModel);
    }

    public SpendingResponse updateSpending(Long id, SpendingRequest spendingRequest) {
        var existingModel = spendingDataService.findById(id);
        spendingMapper.updateModel(spendingRequest, existingModel);
        var updatedModel = spendingDataService.save(existingModel);
        return spendingMapper.toDto(updatedModel);
    }

    public void deleteSpending(Long id) {
        spendingDataService.deleteById(id);
    }
}
