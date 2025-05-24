package com.zeta.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.backend.model.*;
import com.zeta.backend.service.ICardApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardApplicationController.class)
@Slf4j
class CardApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICardApplicationService cardApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfile user;
    private CardApplication application;

    /**
     * Initializes dummy user and card application before each test.
     */
    @BeforeEach
    void setup() {
        user = UserProfile.builder()
                .userId(1L)
                .fullName("sravani")
                .annualIncome(900000.0)
                .build();

        application = CardApplication.builder()
                .id(1L)
                .user(user)
                .cardType("VISA")
                .requestedLimit(200000.0)
                .applicationDate(LocalDate.now())
                .status("APPROVED")
                .build();

        log.info("Setup test data with user ID: {}", user.getUserId());
    }

    /**
     * Test submitting a new card application (positive scenario).
     * Verifies that the controller returns success message with correct user ID.
     */
    @Test
    void applyCard_returnsSuccessMessage() throws Exception {
        // Mocking service layer response
        when(cardApplicationService.apply(Mockito.any(CardApplication.class))).thenReturn(application);

        mockMvc.perform(post("/cards/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isOk())
                .andExpect(content().string("Credit card application submitted successfully for user ID: " + user.getUserId()));

        log.info("Tested successful card application submission");
    }

    /**
     * Test fetching all applications submitted by a specific user.
     * Verifies application fields like cardType, status, limit, and application date.
     */
    @Test
    void getApplicationStatus_returnsListOfApplications() throws Exception {
        List<CardApplication> applications = List.of(application);

        // Mocking service to return one application
        when(cardApplicationService.getApplicationsByUserId(1L)).thenReturn(applications);

        mockMvc.perform(get("/cards/applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardType").value("VISA"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].requestedLimit").value(200000))
                .andExpect(jsonPath("$[0].applicationDate").value(LocalDate.now().toString()));

        log.info("Tested fetching application status by user ID");
    }


}
