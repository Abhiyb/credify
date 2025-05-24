package com.zeta.backend.controller;

import com.zeta.backend.model.CardApplication;
import com.zeta.backend.service.ICardApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Slf4j // Enables SLF4J-based logging
public class CardApplicationController {

    private final ICardApplicationService cardApplicationService;

    /**
     * Endpoint to apply for a credit card
     */
    @PostMapping("/apply")
    public ResponseEntity<String> applyCard(@RequestBody CardApplication cardApplication) {
        log.info("Received credit card application request for user ID: {}", cardApplication.getUser().getUserId());
        CardApplication application = cardApplicationService.apply(cardApplication);
        Long userId = application.getUser().getUserId(); // using userId from UserProfile
        log.info("Credit card application processed successfully for user ID: {}", userId);
        return ResponseEntity.ok("Credit card application submitted successfully for user ID: " + userId);
    }

    /**
     * Endpoint to fetch all card applications for a user
     */
    @GetMapping("/applications/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getApplicationstatus(@PathVariable Long userId) {
        log.info("Fetching card application status for user ID: {}", userId);
        List<CardApplication> applications = cardApplicationService.getApplicationsByUserId(userId);

        // Transform application objects into simplified response structure
        List<Map<String, Object>> response = applications.stream().map(app -> {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("cardType", app.getCardType());
            data.put("status", app.getStatus());
            data.put("requestedLimit", app.getRequestedLimit());
            data.put("applicationDate", app.getApplicationDate());
            return data;
        }).toList();

        log.info("Returning {} application(s) for user ID: {}", response.size(), userId);
        return ResponseEntity.ok(response);
    }
}
