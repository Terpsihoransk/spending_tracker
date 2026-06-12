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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spending.tracker.backend.dto.MenuShoppingListAddRequest;
import spending.tracker.backend.dto.MenuShoppingListResponse;
import spending.tracker.backend.service.MenuShoppingListService;
import spending.tracker.backend.validation.ValidEmailHeader;

@RestController
@RequestMapping("api/v1/menu/shopping-list")
@RequiredArgsConstructor
@Tag(name = "Menu Shopping List", description = "Menu Shopping List management API")
public class MenuShoppingListController {

    private final MenuShoppingListService menuShoppingListService;

    @Operation(summary = "Get shopping list for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shopping list retrieved successfully")
    })
    @GetMapping
    public MenuShoppingListResponse getShoppingList(@Parameter(description = "User email", required = true, example = "user@example.com")
                                                   @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        return menuShoppingListService.getShoppingList(userEmail);
    }

    @Operation(summary = "Add multiple recipes to shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes added successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe or User not found")
    })
    @PostMapping
    public void addRecipes(@Valid @RequestBody MenuShoppingListAddRequest request,
                           @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        menuShoppingListService.addRecipes(userEmail, request.getRecipeIds());
    }

    @Operation(summary = "Add single recipe to shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe added successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe or User not found")
    })
    @PostMapping("/{recipeId}")
    public void addRecipe(@Parameter(description = "Recipe ID", required = true, example = "1")
                          @PathVariable Long recipeId,
                          @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        menuShoppingListService.addRecipe(userEmail, recipeId);
    }

    @Operation(summary = "Remove recipe from shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe removed successfully")
    })
    @DeleteMapping("/{recipeId}")
    public void removeRecipe(@Parameter(description = "Recipe ID", required = true, example = "1")
                             @PathVariable Long recipeId,
                             @ValidEmailHeader @RequestHeader("X-User-Email") String userEmail) {
        menuShoppingListService.removeRecipe(userEmail, recipeId);
    }
}