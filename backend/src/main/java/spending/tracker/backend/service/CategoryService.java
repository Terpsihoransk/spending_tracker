package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.dto.CategoryRequest;
import spending.tracker.backend.dto.CategoryResponse;
import spending.tracker.backend.exception.type.DuplicateCategoryException;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.CategoryMapper;
import spending.tracker.backend.repository.CategoryRepository;
import spending.tracker.backend.repository.UserRepository;
import spending.tracker.backend.service.data.CategoryDataService;
import spending.tracker.backend.service.data.UserDataService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryDataService categoryDataService;
    private final CategoryMapper categoryMapper;
    private final UserDataService userDataService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories(String userEmail) {
        return userDataService.findByEmail(userEmail)
                .map(user -> categoryDataService.findAllByUserId(user.getId()))
                .orElse(List.of())
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id, String userEmail) {
        var user = userDataService.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        var model = categoryDataService.findByIdAndUserId(id, user.getId());
        model.setUserEmail(userEmail);
        return categoryMapper.toDto(model);
    }

    public CategoryResponse createCategory(CategoryRequest categoryRequest, String userEmail) {
        var userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (categoryRepository.existsByUser_IdAndName(userEntity.getId(), categoryRequest.getName())) {
            throw new DuplicateCategoryException(categoryRequest.getName(), userEmail);
        }

        var category = categoryMapper.toEntity(categoryRequest);
        category.setUser(userEntity);
        var savedCategory = categoryRepository.save(category);
        var savedModel = categoryMapper.toModel(savedCategory);
        savedModel.setUserEmail(userEmail);
        return categoryMapper.toDto(savedModel);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest, String userEmail) {
        var user = userDataService.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        var existingModel = categoryDataService.findByIdAndUserId(id, user.getId());

        if (!existingModel.getName().equals(categoryRequest.getName()) &&
                categoryRepository.existsByUser_IdAndName(user.getId(), categoryRequest.getName())) {
            throw new DuplicateCategoryException(categoryRequest.getName(), userEmail);
        }

        existingModel.setName(categoryRequest.getName());
        existingModel.setUserEmail(userEmail);
        var updatedModel = categoryDataService.save(existingModel);
        return categoryMapper.toDto(updatedModel);
    }

    public void deleteCategory(Long id, String userEmail) {
        var user = userDataService.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        var category = categoryDataService.findByIdAndUserId(id, user.getId());
        if (!category.getUserEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Category", "id", id.toString());
        }
        categoryDataService.deleteById(id);
    }
}