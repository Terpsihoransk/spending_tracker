package spending.tracker.backend.controller;

import spending.tracker.backend.model.Spending;
import spending.tracker.backend.service.SpendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spendings")
public class SpendingController {

    @Autowired
    private SpendingService spendingService;

    @GetMapping
    public List<Spending> getAllSpendings(@AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttribute("email");
        return spendingService.getAllSpendings(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Spending> getSpendingById(@PathVariable Long id) {
        return spendingService.getSpendingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Spending createSpending(@RequestBody Spending spending, @AuthenticationPrincipal OAuth2User principal) {
        spending.setUserId(principal.getAttribute("email"));
        return spendingService.createSpending(spending);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Spending> updateSpending(@PathVariable Long id, @RequestBody Spending spendingDetails) {
        Spending updatedSpending = spendingService.updateSpending(id, spendingDetails);
        if (updatedSpending != null) {
            return ResponseEntity.ok(updatedSpending);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpending(@PathVariable Long id) {
        spendingService.deleteSpending(id);
        return ResponseEntity.noContent().build();
    }
}