package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spending.tracker.backend.dto.SpendingRequest;
import spending.tracker.backend.dto.SpendingResponse;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.SpendingMapper;
import spending.tracker.backend.service.data.SpendingDataService;
import spending.tracker.backend.service.data.UserDataService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpendingService {

    private final SpendingDataService spendingDataService;
    private final SpendingMapper spendingMapper;
    private final UserDataService userDataService;

    @Transactional(readOnly = true)
    public List<SpendingResponse> getAllSpending(String userEmail) {
        // Маппинг entity -> model выполняется ровно один раз внутри
        // SpendingDataService#findAllByUserId, в его @Transactional(readOnly = true).
        // Здесь остаётся только преобразование готовых моделей в response DTO,
        // повторного чтения lazy-ассоциаций через маппер не происходит.
        return userDataService.findByEmail(userEmail)
                .map(user -> spendingDataService.findAllByUserId(user.getId()))
                .orElse(List.of())
                .stream()
                .map(spendingMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
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
        return spendingMapper.toDto(savedModel);
    }

    public SpendingResponse updateSpending(Long id, SpendingRequest spendingRequest, String userEmail) {
        var existingModel = spendingDataService.findById(id);
        spendingMapper.updateModel(spendingRequest, existingModel, userEmail);
        existingModel.setUserEmail(userEmail);
        if (spendingRequest.getDate() != null) {
            existingModel.setDate(spendingRequest.getDate());
        }
        var updatedModel = spendingDataService.updateSpending(existingModel);
        return spendingMapper.toDto(updatedModel);
    }

    public void deleteSpending(Long id, String userEmail) {
        var spending = spendingDataService.findById(id);
        if (!spending.getUserEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Spending", "id", id.toString());
        }
        spendingDataService.deleteById(id);
    }
}
