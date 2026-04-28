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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spending.tracker.backend.dto.SubCategoryRequest;
import spending.tracker.backend.dto.SubCategoryResponse;
import spending.tracker.backend.service.SubCategoryService;
import spending.tracker.backend.validation.ValidEmailHeader;

import java.util.List;

@RestController
@RequestMapping("api/v1/subcategories")
@RequiredArgsConstructor
@Tag(name = "SubCategory", description = "SubCategory management API")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @Operation(summary = "Get all subcategories by category ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubCategories retrieved successfully")
    })
    @GetMapping
    public List<SubCategoryResponse> getAllByCategory(
            @Parameter(description = "Category ID", required = true, example = "1")
            @RequestParam Long categoryId,
            @Parameter(description = "User email", required = true, example = "user@example.com")
            @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return subCategoryService.getAllByCategory(categoryId, userEmail);
    }

    @Operation(summary = "Create new subcategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubCategory created successfully"),
            @ApiResponse(responseCode = "409", description = "SubCategory with this name already exists for user")
    })
    @PostMapping
    public SubCategoryResponse create(
            @Parameter(description = "User email", required = true, example = "user@example.com")
            @Valid @RequestBody SubCategoryRequest request,
            @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return subCategoryService.create(request, userEmail);
    }

    @Operation(summary = "Update existing subcategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubCategory updated successfully"),
            @ApiResponse(responseCode = "404", description = "SubCategory not found"),
            @ApiResponse(responseCode = "409", description = "SubCategory with this name already exists for user")
    })
    @PutMapping("/{id}")
    public SubCategoryResponse update(
            @Parameter(description = "SubCategory ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryRequest request,
            @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return subCategoryService.update(id, request, userEmail);
    }

    @Operation(summary = "Delete subcategory by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubCategory deleted"),
            @ApiResponse(responseCode = "404", description = "SubCategory not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete subcategory with existing spendings")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "SubCategory ID", required = true, example = "1")
            @PathVariable Long id,
            @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        subCategoryService.delete(id, userEmail);
    }
}