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
import spending.tracker.backend.dto.SpendingRequest;
import spending.tracker.backend.dto.SpendingResponse;
import spending.tracker.backend.service.SpendingService;

import java.util.List;

@RestController
@RequestMapping("api/v1/spending")
@RequiredArgsConstructor
@Tag(name = "Spending", description = "Spending management API")
public class SpendingController {

    private final SpendingService spendingService;

    @Operation(summary = "Get all spendings for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spendings retrieved successfully")
    })
    @GetMapping
    public List<SpendingResponse> getAllSpending(@Parameter(description = "User email", required = true, example = "user@example.com")
                                                 @RequestHeader("X-User-Email") String userEmail) {
        return spendingService.getAllSpending(userEmail);
    }

    @Operation(summary = "Get spending by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending found"),
            @ApiResponse(responseCode = "404", description = "Spending not found")
    })
    @GetMapping("/{id}")
    public SpendingResponse getSpendingById(@Parameter(description = "Spending ID", required = true, example = "1")
                                            @PathVariable Long id,
                                            @RequestHeader("X-User-Email") String userEmail) {
        return spendingService.getSpendingById(id, userEmail);
    }

    @Operation(summary = "Create new spending")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending created successfully")
    })
    @PostMapping
    public SpendingResponse createSpending(@Parameter(description = "User email", required = true, example = "user@example.com")
                                           @Valid @RequestBody SpendingRequest spendingRequest,
                                           @RequestHeader("X-User-Email") String userEmail) {
        return spendingService.createSpending(spendingRequest, userEmail);
    }

    @Operation(summary = "Update existing spending")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending updated successfully"),
            @ApiResponse(responseCode = "404", description = "Spending not found")
    })
    @PutMapping("/{id}")
    public SpendingResponse updateSpending(@Parameter(description = "Spending ID", required = true, example = "1")
                                           @PathVariable Long id,
                                           @Valid @RequestBody SpendingRequest spendingRequest,
                                           @RequestHeader("X-User-Email") String userEmail) {
        return spendingService.updateSpending(id, spendingRequest, userEmail);
    }

    @Operation(summary = "Delete spending by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending deleted"),
            @ApiResponse(responseCode = "404", description = "Spending not found")
    })
    @DeleteMapping("/{id}")
    public void deleteSpending(@Parameter(description = "Spending ID", required = true, example = "1")
                               @PathVariable Long id,
                               @RequestHeader("X-User-Email") String userEmail) {
        spendingService.deleteSpending(id, userEmail);
    }
}