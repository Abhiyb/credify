package com.zeta.backend.controller;

import com.zeta.backend.dto.CardApplicationResponseDTO;
import com.zeta.backend.model.CardApplication;
import com.zeta.backend.service.ICardApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Slf4j
public class CardApplicationController {

    private final ICardApplicationService cardApplicationService;

    /**
     * APPLY FOR CARD (JWT secured)
     * userId should not come from frontend
     */
    @PostMapping("/apply")
    public ResponseEntity<String> applyCard(Authentication authentication,
                                            @RequestBody CardApplication cardApplication) {

        String email = authentication.getName();
        log.info("Received credit card application request from logged-in user: {}", email);

        CardApplication application = cardApplicationService.apply(email, cardApplication);

        log.info("Credit card application processed successfully for user: {}", email);

        return ResponseEntity.ok("Credit card application submitted successfully for user: " + email);
    }

    /**
     * FETCH ALL APPLICATIONS OF LOGGED-IN USER
     */
    @GetMapping("/applications/me")
    public ResponseEntity<List<CardApplicationResponseDTO>> getMyApplications(Authentication authentication) {

        String email = authentication.getName();
        log.info("Fetching card applications for logged-in user: {}", email);

        List<CardApplication> applications = cardApplicationService.getApplicationsByEmail(email);

        List<CardApplicationResponseDTO> response = applications.stream()
                .map(app -> CardApplicationResponseDTO.builder()
                        .cardType(app.getCardType())
                        .status(app.getStatus())
                        .requestedLimit(app.getRequestedLimit())
                        .applicationDate(app.getApplicationDate())
                        .build())
                .toList();

        log.info("Returning {} applications for user {}", response.size(), email);

        return ResponseEntity.ok(response);
    }


    /**
     * FETCH SINGLE APPLICATION BY ID (JWT secured)
     * User should only access his own application
     */
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<CardApplication> getApplicationById(Authentication authentication,
                                                              @PathVariable Long applicationId) {

        String email = authentication.getName();
        log.info("Fetching application ID {} for user {}", applicationId, email);

        CardApplication application = cardApplicationService.getApplicationById(email, applicationId);

        return ResponseEntity.ok(application);
    }

    /**
     * UPDATE APPLICATION (JWT secured)
     * User should update only his own application
     */
    @PutMapping("/update/{applicationId}")
    public ResponseEntity<CardApplication> updateApplication(Authentication authentication,
                                                             @PathVariable Long applicationId,
                                                             @RequestBody CardApplication updatedApplication) {

        String email = authentication.getName();
        log.info("Updating application ID {} for user {}", applicationId, email);

        CardApplication application = cardApplicationService.updateApplication(email, applicationId, updatedApplication);

        return ResponseEntity.ok(application);
    }

    /**
     * DELETE APPLICATION (JWT secured)
     * User should delete only his own application
     */
//    @DeleteMapping("/delete/{applicationId}")
//    public ResponseEntity<String> deleteApplication(Authentication authentication,
//                                                    @PathVariable Long applicationId) {
//
//        String email = authentication.getName();
//        log.info("Deleting application ID {} for user {}", applicationId, email);
//
//        cardApplicationService.deleteApplication(email, applicationId);
//
//        return ResponseEntity.ok("Application deleted successfully for ID: " + applicationId);
//    }
}
