package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.exception.ResourceNotFoundException;
import spending.tracker.backend.mapper.SpendingMapper;
import spending.tracker.backend.model.SpendingModel;
import spending.tracker.backend.repository.SpendingRepository;
import spending.tracker.backend.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpendingDataService {

    private final SpendingRepository spendingRepository;
    private final UserRepository userRepository;
    private final SpendingMapper spendingMapper;

    public List<SpendingModel> findAllByUserId(Long userId) {
        return spendingRepository.findByUser_Id(userId).stream()
                .map(spendingMapper::toModel)
                .toList();
    }

    public SpendingModel findById(Long id) {
        return spendingRepository.findById(id)
                .map(spendingMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("Spending", "id", id));
    }

    public SpendingModel save(SpendingModel spendingModel, Long userId) {
        var spending = spendingMapper.toEntity(spendingModel);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        spending.setUser(user);
        var savedSpending = spendingRepository.save(spending);
        return spendingMapper.toModel(savedSpending);
    }

    public SpendingModel save(SpendingModel spendingModel) {
        var spending = spendingMapper.toEntity(spendingModel);
        if (spending.getUser() == null && spendingModel.getUserEmail() != null) {
            var user = userRepository.findByEmail(spendingModel.getUserEmail());
            if (user == null) {
                throw new ResourceNotFoundException("User", "email", spendingModel.getUserEmail());
            }
            spending.setUser(user);
        }
        var savedSpending = spendingRepository.save(spending);
        return spendingMapper.toModel(savedSpending);
    }

    public void deleteById(Long id) {
        if (!spendingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Spending", "id", id);
        }
        spendingRepository.deleteById(id);
    }
}
