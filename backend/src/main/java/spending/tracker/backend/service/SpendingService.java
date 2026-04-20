package spending.tracker.backend.service;

import spending.tracker.backend.model.Spending;
import spending.tracker.backend.repository.SpendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpendingService {

    @Autowired
    private SpendingRepository spendingRepository;

    public List<Spending> getAllSpendings(String userId) {
        return spendingRepository.findByUserId(userId);
    }

    public Optional<Spending> getSpendingById(Long id) {
        return spendingRepository.findById(id);
    }

    public Spending createSpending(Spending spending) {
        return spendingRepository.save(spending);
    }

    public Spending updateSpending(Long id, Spending spendingDetails) {
        Optional<Spending> optionalSpending = spendingRepository.findById(id);
        if (optionalSpending.isPresent()) {
            Spending spending = optionalSpending.get();
            spending.setAmount(spendingDetails.getAmount());
            spending.setCategory(spendingDetails.getCategory());
            spending.setDate(spendingDetails.getDate());
            spending.setDescription(spendingDetails.getDescription());
            return spendingRepository.save(spending);
        }
        return null;
    }

    public void deleteSpending(Long id) {
        spendingRepository.deleteById(id);
    }
}