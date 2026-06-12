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
import spending.tracker.backend.dto.MenuIngredientRequest;
import spending.tracker.backend.dto.MenuIngredientResponse;
import spending.tracker.backend.dto.MenuMeasureResponse;
import spending.tracker.backend.dto.MenuRecipeRequest;
import spending.tracker.backend.dto.MenuRecipeResponse;
import spending.tracker.backend.service.MenuIngredientService;
import spending.tracker.backend.service.MenuMeasureService;
import spending.tracker.backend.service.MenuRecipeService;
import spending.tracker.backend.validation.ValidEmailHeader;

import java.util.List;

@RestController
@RequestMapping("api/v1/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "Menu, Recipes, and Ingredients management API")
public class MenuController {

    private final MenuRecipeService menuRecipeService;
    private final MenuIngredientService menuIngredientService;
    private final MenuMeasureService menuMeasureService;

    @Operation(summary = "Get all recipes for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes retrieved successfully")
    })
    @GetMapping("/recipes")
    public List<MenuRecipeResponse> getAllRecipes(@Parameter(description = "User email", required = true, example = "user@example.com")
                                                 @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return menuRecipeService.getAllRecipes(userEmail);
    }

    @Operation(summary = "Get recipe by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe found"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @GetMapping("/recipes/{id}")
    public MenuRecipeResponse getRecipeById(@Parameter(description = "Recipe ID", required = true, example = "1")
                                           @PathVariable Long id,
                                           @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return menuRecipeService.getRecipeById(id, userEmail);
    }

    @Operation(summary = "Create new recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "409", description = "Recipe with this name already exists")
    })
    @PostMapping("/recipes")
    public MenuRecipeResponse createRecipe(@Valid @RequestBody MenuRecipeRequest request,
                                           @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return menuRecipeService.createRecipe(request, userEmail);
    }

    @Operation(summary = "Update existing recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found"),
            @ApiResponse(responseCode = "409", description = "Recipe with this name already exists")
    })
    @PutMapping("/recipes/{id}")
    public MenuRecipeResponse updateRecipe(@Parameter(description = "Recipe ID", required = true, example = "1")
                                           @PathVariable Long id,
                                           @Valid @RequestBody MenuRecipeRequest request,
                                           @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return menuRecipeService.updateRecipe(id, request, userEmail);
    }

    @Operation(summary = "Delete recipe by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe deleted"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @DeleteMapping("/recipes/{id}")
    public void deleteRecipe(@Parameter(description = "Recipe ID", required = true, example = "1")
                             @PathVariable Long id,
                             @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        menuRecipeService.deleteRecipe(id, userEmail);
    }

    @Operation(summary = "Get all ingredients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully")
    })
    @GetMapping("/ingredients")
    public List<MenuIngredientResponse> getAllIngredients() {
        return menuIngredientService.getAllIngredients();
    }

    @Operation(summary = "Get ingredient by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient found"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found")
    })
    @GetMapping("/ingredients/{id}")
    public MenuIngredientResponse getIngredientById(@Parameter(description = "Ingredient ID", required = true, example = "1")
                                                   @PathVariable Long id) {
        return menuIngredientService.getIngredientById(id);
    }

    @Operation(summary = "Create new ingredient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient created successfully"),
            @ApiResponse(responseCode = "409", description = "Ingredient with this name already exists")
    })
    @PostMapping("/ingredients")
    public MenuIngredientResponse createIngredient(@Valid @RequestBody MenuIngredientRequest request) {
        return menuIngredientService.createIngredient(request);
    }

    @Operation(summary = "Delete ingredient by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient deleted"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete ingredient because it is in use")
    })
    @DeleteMapping("/ingredients/{id}")
    public void deleteIngredient(@Parameter(description = "Ingredient ID", required = true, example = "1")
                                 @PathVariable Long id) {
        menuIngredientService.deleteIngredient(id);
    }

    @Operation(summary = "Get all measures")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Measures retrieved successfully")
    })
    @GetMapping("/measures")
    public List<MenuMeasureResponse> getAllMeasures() {
        return menuMeasureService.getAllMeasures();
    }

    @Operation(summary = "Get measure by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Measure found"),
            @ApiResponse(responseCode = "404", description = "Measure not found")
    })
    @GetMapping("/measures/{id}")
    public MenuMeasureResponse getMeasureById(@Parameter(description = "Measure ID", required = true, example = "1")
                                             @PathVariable Long id) {
        return menuMeasureService.getMeasureById(id);
    }
}