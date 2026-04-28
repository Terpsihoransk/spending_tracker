package spending.tracker.backend.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spending.tracker.backend.exception.CategoryInUseException;
import spending.tracker.backend.exception.ResourceNotFoundException;
import spending.tracker.backend.mapper.CategoryMapper;
import spending.tracker.backend.model.CategoryModel;
import spending.tracker.backend.repository.CategoryRepository;
import spending.tracker.backend.repository.SpendingRepository;
import spending.tracker.backend.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryDataService {

    private final CategoryRepository categoryRepository;
    private final SpendingRepository spendingRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryModel> findAllByUserId(Long userId) {
        return categoryRepository.findByUser_Id(userId).stream()
                .map(categoryMapper::toModel)
                .toList();
    }

    public CategoryModel findByIdAndUserId(Long id, Long userId) {
        return categoryRepository.findByIdAndUser_Id(id, userId)
                .map(categoryMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    public CategoryModel findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    public CategoryModel save(CategoryModel categoryModel) {
        var category = categoryMapper.toEntity(categoryModel);
        if (category.getUser() == null && categoryModel.getUserEmail() != null) {
            var user = userRepository.findByEmail(categoryModel.getUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", categoryModel.getUserEmail()));
            category.setUser(user);
        }
        var savedCategory = categoryRepository.save(category);
        return categoryMapper.toModel(savedCategory);
    }

    public void deleteById(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (spendingRepository.existsByCategory_Id(id)) {
            throw new CategoryInUseException(
                    String.format("Cannot delete category with id %d because it is referenced by existing spendings", id),
                    id);
        }

        categoryRepository.deleteById(id);
    }
}