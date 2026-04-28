package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.SpendingMapper;
import spending.tracker.backend.model.SpendingModel;
import spending.tracker.backend.repository.CategoryRepository;
import spending.tracker.backend.repository.SpendingRepository;
import spending.tracker.backend.repository.SubCategoryRepository;
import spending.tracker.backend.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpendingDataService {

    private final SpendingRepository spendingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
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

        var category = categoryRepository.findById(spendingModel.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", spendingModel.getCategoryId()));
        spending.setCategory(category);

        if (spendingModel.getSubcategoryId() != null) {
            var subCategory = subCategoryRepository.findById(spendingModel.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", spendingModel.getSubcategoryId()));
            spending.setSubCategory(subCategory);
        }

        var savedSpending = spendingRepository.save(spending);
        return spendingMapper.toModel(savedSpending);
    }

    public SpendingModel updateSpending(SpendingModel spendingModel) {
        var spending = spendingMapper.toEntity(spendingModel);
        if (spending.getUser() == null && spendingModel.getUserEmail() != null) {
            var user = userRepository.findByEmail(spendingModel.getUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", spendingModel.getUserEmail()));
            spending.setUser(user);
        }
        if (spendingModel.getCategoryId() != null) {
            var category = categoryRepository.findById(spendingModel.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", spendingModel.getCategoryId()));
            spending.setCategory(category);
        }
        if (spendingModel.getSubcategoryId() != null) {
            var subCategory = subCategoryRepository.findById(spendingModel.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", spendingModel.getSubcategoryId()));
            spending.setSubCategory(subCategory);
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
