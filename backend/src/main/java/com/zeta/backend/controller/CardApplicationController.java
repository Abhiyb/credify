package com.zeta.backend.controller;
import com.zeta.backend.model.CardApplication;
import com.zeta.backend.service.ICardApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardApplicationController {

    private final ICardApplicationService cardApplicationService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyCard(@RequestBody CardApplication cardApplication) {
        CardApplication application = cardApplicationService.apply(cardApplication);
        Long userId = application.getUser().getUserId(); // using userId from UserProfile
        return ResponseEntity.ok("Credit card application submitted successfully for user ID: " + userId);
    }


    @GetMapping("/applications/{userId}")
    public ResponseEntity<Map<String, Object>> getApplicationstatus(@PathVariable Long userId) {
        CardApplication application = cardApplicationService.getApplicationsByUserId(userId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("cardType", application.getCardType());
        response.put("status", application.getStatus());
        response.put("requestedLimit", application.getRequestedLimit());

        return ResponseEntity.ok(response);
    }

}
