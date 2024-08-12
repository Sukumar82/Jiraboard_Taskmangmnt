package org.example.jiraboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        // You can add more complex checks here if needed, like checking database connectivity, etc.
        return ResponseEntity.ok("Application is running");
    }
}
