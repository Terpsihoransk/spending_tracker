package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.exception.ResourceNotFoundException;
import spending.tracker.backend.mapper.SpendingMapper;
import spending.tracker.backend.model.SpendingModel;
import spending.tracker.backend.repository.SpendingRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpendingDataService {

    private final SpendingRepository spendingRepository;
    private final SpendingMapper spendingMapper;

    public List<SpendingModel> findAllByUserEmail(String userEmail) {
        return spendingRepository.findByUserEmail(userEmail).stream()
                .map(spendingMapper::toModel)
                .toList();
    }

    public SpendingModel findById(Long id) {
        return spendingRepository.findById(id)
                .map(spendingMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("Spending", "id", id));
    }

    public SpendingModel save(SpendingModel spendingModel) {
        Spending spending = spendingMapper.toEntity(spendingModel);
        Spending savedSpending = spendingRepository.save(spending);
        return spendingMapper.toModel(savedSpending);
    }

    public void deleteById(Long id) {
        if (!spendingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Spending", "id", id);
        }
        spendingRepository.deleteById(id);
    }
}
