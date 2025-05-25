package com.zeta.backend.controller;

import com.zeta.backend.dto.CardApplicationResponseDto; // 🔄 MODIFIED
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
    public ResponseEntity<List<CardApplicationResponseDto>> getApplicationstatus(@PathVariable Long userId) {
        log.info("Fetching card application status for user ID: {}", userId);
        List<CardApplication> applications = cardApplicationService.getApplicationsByUserId(userId);

        // 🔄 Use dto
        List<CardApplicationResponseDto> response = applications.stream()
                .map(app -> CardApplicationResponseDto.builder()
                        .cardType(app.getCardType())
                        .status(app.getStatus())
                        .requestedLimit(app.getRequestedLimit())
                        .applicationDate(app.getApplicationDate())
                        .build())
                .toList();

        log.info("Returning {} application(s) for user ID: {}", response.size(), userId);
        return ResponseEntity.ok(response);
    }
}
