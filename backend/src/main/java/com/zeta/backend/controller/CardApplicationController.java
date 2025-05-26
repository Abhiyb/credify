package com.zeta.backend.controller;

import com.zeta.backend.dto.CardApplicationResponseDTO; // 🔄 MODIFIED
import com.zeta.backend.model.CardApplication;
import com.zeta.backend.service.ICardApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Slf4j
public class CardApplicationController {

    private final ICardApplicationService cardApplicationService;

    /**
     * Endpoint to apply for a credit card
     */
    @PostMapping("/apply")
    public ResponseEntity<String> applyCard(@RequestBody CardApplication cardApplication) {
        log.info("Received credit card application request for user ID: {}", cardApplication.getUser().getUserId());
        CardApplication application = cardApplicationService.apply(cardApplication);
        Long userId = application.getUser().getUserId();
        log.info("Credit card application processed successfully for user ID: {}", userId);
        return ResponseEntity.ok("Credit card application submitted successfully for user ID: " + userId);
    }

    /**
     * Endpoint to fetch all card applications for a user
     */
    @GetMapping("/applications/{userId}")
    public ResponseEntity<List<CardApplicationResponseDTO>> getApplicationstatus(@PathVariable Long userId) {
        log.info("Fetching card application status for user ID: {}", userId);
        List<CardApplication> applications = cardApplicationService.getApplicationsByUserId(userId);

        // 🔄 Use dto
        List<CardApplicationResponseDTO> response = applications.stream()
                .map(app -> CardApplicationResponseDTO.builder()
                        .cardType(app.getCardType())
                        .status(app.getStatus())
                        .requestedLimit(app.getRequestedLimit())
                        .applicationDate(app.getApplicationDate())
                        .build())
                .toList();

        log.info("Returning {} application(s) for user ID: {}", response.size(), userId);
        return ResponseEntity.ok(response);
    }
    /**
     * READ Operation
     * Fetch a specific application by its ID
     */
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<CardApplication> getApplicationById(@PathVariable Long applicationId) {
        log.info("🔍 Fetching card application by ID: {}", applicationId);

        CardApplication application = cardApplicationService.getApplicationById(applicationId);

        log.info("📄 Application found for ID: {}", applicationId);
        return ResponseEntity.ok(application);
    }

    /**
     * UPDATE Operation
     * Update an existing credit card application
     */
    @PutMapping("/update/{applicationId}")
    public ResponseEntity<CardApplication> updateApplication(
            @PathVariable Long applicationId,
            @RequestBody CardApplication updatedApplication) {

        log.info("♻️ Updating card application with ID: {}", applicationId);

        CardApplication application = cardApplicationService.updateApplication(applicationId, updatedApplication);

        log.info("✅ Application updated successfully for ID: {}", applicationId);
        return ResponseEntity.ok(application);
    }

    /**
     * DELETE Operation
     * Delete an application by its ID
     */
    @DeleteMapping("/delete/{applicationId}")
    public ResponseEntity<String> deleteApplication(@PathVariable Long applicationId) {
        log.info("❌ Deleting card application with ID: {}", applicationId);

        cardApplicationService.deleteApplication(applicationId);

        log.info("🗑️ Application deleted successfully for ID: {}", applicationId);
        return ResponseEntity.ok("Application deleted successfully for ID: " + applicationId);
    }



}
