package spending.tracker.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spending.tracker.backend.dto.CategoryRequest;
import spending.tracker.backend.dto.CategoryResponse;
import spending.tracker.backend.service.CategoryService;
import spending.tracker.backend.validation.ValidEmailHeader;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category management API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping
    public List<CategoryResponse> getAllCategories(@Parameter(description = "User email", required = true, example = "user@example.com")
                                                 @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return categoryService.getAllCategories(userEmail);
    }

    @Operation(summary = "Get category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@Parameter(description = "Category ID", required = true, example = "1")
                                          @PathVariable Long id,
                                          @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return categoryService.getCategoryById(id, userEmail);
    }

    @Operation(summary = "Create new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "409", description = "Category with this name already exists for user")
    })
    @PostMapping
    public CategoryResponse createCategory(@Parameter(description = "User email", required = true, example = "user@example.com")
                                         @Valid @RequestBody CategoryRequest categoryRequest,
                                         @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return categoryService.createCategory(categoryRequest, userEmail);
    }

    @Operation(summary = "Update existing category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category with this name already exists for user")
    })
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@Parameter(description = "Category ID", required = true, example = "1")
                                         @PathVariable Long id,
                                         @Valid @RequestBody CategoryRequest categoryRequest,
                                         @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return categoryService.updateCategory(id, categoryRequest, userEmail);
    }

    @Operation(summary = "Delete category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete category with existing spendings")
    })
    @DeleteMapping("/{id}")
    public void deleteCategory(@Parameter(description = "Category ID", required = true, example = "1")
                               @PathVariable Long id,
                               @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        categoryService.deleteCategory(id, userEmail);
    }
}