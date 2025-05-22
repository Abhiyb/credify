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
@CrossOrigin(origins = "http://localhost:5173")
public class CardApplicationController {

    private final ICardApplicationService cardApplicationService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyCard(@RequestBody CardApplication cardApplication) {
        CardApplication application = cardApplicationService.apply(cardApplication);
        Long userId = application.getUser().getUserId(); // using userId from UserProfile
        return ResponseEntity.ok("Credit card application submitted successfully for user ID: " + userId);
    }


    @GetMapping("/applications/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getApplicationstatus(@PathVariable Long userId) {
        List<CardApplication> applications = cardApplicationService.getApplicationsByUserId(userId);

        List<Map<String, Object>> response = applications.stream().map(app -> {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("cardType", app.getCardType());
            data.put("status", app.getStatus());
            data.put("requestedLimit", app.getRequestedLimit());
            data.put("applicationDate", app.getApplicationDate());
            return data;
        }).toList();

        return ResponseEntity.ok(response);
    }
}
