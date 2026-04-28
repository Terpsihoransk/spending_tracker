package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.mapper.SpendingMapper;
import spending.tracker.backend.model.SpendingDto;
import spending.tracker.backend.model.SpendingModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpendingService {

    private final SpendingDataService spendingDataService;
    private final SpendingMapper spendingMapper;

    public List<SpendingDto> getAllSpending(String userId) {
        return spendingDataService.findAllByUserId(userId).stream()
                .map(spendingMapper::toDto)
                .toList();
    }

    public SpendingDto getSpendingById(Long id) {
        SpendingModel model = spendingDataService.findById(id);
        return spendingMapper.toDto(model);
    }

    public SpendingDto createSpending(SpendingDto spendingDto) {
        SpendingModel model = spendingMapper.toModel(spendingDto);
        SpendingModel savedModel = spendingDataService.save(model);
        return spendingMapper.toDto(savedModel);
    }

    public SpendingDto updateSpending(Long id, SpendingDto spendingDto) {
        SpendingModel existingModel = spendingDataService.findById(id);
        spendingMapper.updateModel(spendingDto, existingModel);
        SpendingModel updatedModel = spendingDataService.save(existingModel);
        return spendingMapper.toDto(updatedModel);
    }

    public void deleteSpending(Long id) {
        spendingDataService.deleteById(id);
    }
}
