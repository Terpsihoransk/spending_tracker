package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.entity.SubCategory;
import spending.tracker.backend.mapper.SubCategoryMapper;
import spending.tracker.backend.model.SubCategoryModel;
import spending.tracker.backend.repository.CategoryRepository;
import spending.tracker.backend.repository.SpendingRepository;
import spending.tracker.backend.repository.SubCategoryRepository;
import spending.tracker.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SubCategoryDataService {

    private final SubCategoryRepository subCategoryRepository;
    private final SpendingRepository spendingRepository;
    private final SubCategoryMapper subCategoryMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public SubCategoryModel findById(Long id) {
        return subCategoryRepository.findById(id)
                .map(subCategoryMapper::toModel)
                .orElse(null);
    }

    public SubCategoryModel findByIdAndUserId(Long id, Long userId) {
        return subCategoryRepository.findByIdAndUser_Id(id, userId)
                .map(subCategoryMapper::toModel)
                .orElse(null);
    }

    public SubCategoryModel save(SubCategoryModel model) {
        SubCategory entity = subCategoryMapper.toEntity(model);
        
        if (entity.getUser() == null && model.getUserEmail() != null) {
            var user = userRepository.findByEmail(model.getUserEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            entity.setUser(user);
        }
        
        if (entity.getCategory() == null && model.getCategoryId() != null) {
            var category = categoryRepository.findById(model.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            entity.setCategory(category);
        }
        
        SubCategory saved = subCategoryRepository.save(entity);
        return subCategoryMapper.toModel(saved);
    }

    public void deleteById(Long id) {
        subCategoryRepository.deleteById(id);
    }

    public boolean existsBySubCategoryInSpendings(Long subCategoryId) {
        return spendingRepository.existsBySubCategory_Id(subCategoryId);
    }
}