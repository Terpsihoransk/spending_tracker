package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.dto.SubCategoryRequest;
import spending.tracker.backend.dto.SubCategoryResponse;
import spending.tracker.backend.exception.type.CategoryInUseException;
import spending.tracker.backend.exception.type.DuplicateSubCategoryException;
import spending.tracker.backend.exception.type.ResourceNotFoundException;
import spending.tracker.backend.mapper.SubCategoryMapper;
import spending.tracker.backend.model.CategoryModel;
import spending.tracker.backend.model.SubCategoryModel;
import spending.tracker.backend.repository.CategoryRepository;
import spending.tracker.backend.repository.SubCategoryRepository;
import spending.tracker.backend.repository.UserRepository;
import spending.tracker.backend.service.data.CategoryDataService;
import spending.tracker.backend.service.data.SubCategoryDataService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final SubCategoryMapper subCategoryMapper;
    private final SubCategoryDataService subCategoryDataService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryDataService categoryDataService;

    public SubCategoryResponse create(SubCategoryRequest request, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CategoryModel category = categoryDataService.findByIdAndUserId(request.getCategoryId(), user.getId());
        if (category == null) {
            throw new ResourceNotFoundException("Category not found");
        }

        if (subCategoryRepository.existsByCategory_IdAndNameAndUser_Email(request.getCategoryId(), request.getName(), userEmail)) {
            throw new DuplicateSubCategoryException(request.getName(), userEmail);
        }

        SubCategoryModel model = SubCategoryModel.builder()
                .categoryId(request.getCategoryId())
                .categoryName(category.getName())
                .name(request.getName())
                .userEmail(userEmail)
                .build();

        SubCategoryModel saved = subCategoryDataService.save(model);
        return subCategoryMapper.toDto(saved);
    }

    public List<SubCategoryResponse> getAllByCategory(Long categoryId, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CategoryModel category = categoryDataService.findByIdAndUserId(categoryId, user.getId());
        if (category == null) {
            throw new ResourceNotFoundException("Category not found");
        }

        List<SubCategoryModel> subCategories = subCategoryRepository.findAllByCategory_IdAndUser_Email(categoryId, userEmail)
                .stream()
                .map(subCategoryMapper::toModel)
                .toList();

        return subCategories.stream()
                .map(subCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public SubCategoryResponse update(Long id, SubCategoryRequest request, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SubCategoryModel existing = subCategoryDataService.findByIdAndUserId(id, user.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("SubCategory not found");
        }

        if (!existing.getCategoryId().equals(request.getCategoryId())) {
            CategoryModel newCategory = categoryDataService.findByIdAndUserId(request.getCategoryId(), user.getId());
            if (newCategory == null) {
                throw new ResourceNotFoundException("Category not found");
            }
        }

        if (subCategoryRepository.existsByCategory_IdAndNameAndIdNot(request.getCategoryId(), request.getName(), id)) {
            throw new DuplicateSubCategoryException(request.getName(), userEmail);
        }

        subCategoryMapper.updateModel(request, existing);
        SubCategoryModel updated = subCategoryDataService.save(existing);
        return subCategoryMapper.toDto(updated);
    }

    public void delete(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SubCategoryModel existing = subCategoryDataService.findByIdAndUserId(id, user.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("SubCategory not found");
        }

        if (subCategoryDataService.existsBySubCategoryInSpendings(id)) {
            throw new CategoryInUseException(
                    String.format("Cannot delete subcategory with id %d because it is used by existing spendings", id),
                    id);
        }

        subCategoryDataService.deleteById(id);
    }
}