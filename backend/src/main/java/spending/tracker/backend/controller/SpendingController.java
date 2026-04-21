package spending.tracker.backend.controller;

import spending.tracker.backend.entity.Spending;
import spending.tracker.backend.service.SpendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/spendings")
public class SpendingController {

    @Autowired
    private SpendingService spendingService;

    @GetMapping
    public List<Spending> getAllSpendings(@RequestHeader("X-User-Email") String userId) {
        return spendingService.getAllSpendings(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Spending> getSpendingById(@PathVariable Long id) {
        return spendingService.getSpendingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Spending createSpending(@RequestBody Spending spending, @RequestHeader("X-User-Email") String userId) {
        spending.setUserId(userId);
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