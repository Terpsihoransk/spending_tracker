package spending.tracker.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
            "name", "Spending Tracker API",
            "version", "v0.0.1",
            "description", "Backend API for personal expense tracking application",
            "endpoints", Map.of(
                "swagger", "/swagger-ui/index.html"
            )
        ));
    }
}