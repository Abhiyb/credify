package com.zeta.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.backend.dto.CardDTO;
import com.zeta.backend.service.ICardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig
@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICardService cardService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CardDTO mockCardDTO() {
        return CardDTO.builder()
                .cardId(1L)
                .cardNumber("1234567890123456")
                .cardType("VISA")
                .status("ACTIVE")
                .creditLimit(10000.0)
                .availableLimit(8000.0)
                .expiryDate(LocalDate.now().plusYears(2))
                .cardHolderName("John Doe")
                .build();
    }

    @Test
    void testGetCardByUserId() throws Exception {
        when(cardService.getCardDetailsByUserId(1L))
                .thenReturn(List.of(mockCardDTO()));

        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardHolderName").value("John Doe"));
    }

    @Test
    void testPutCardByUserId() throws Exception {
        CardDTO updatedCard = mockCardDTO();
        updatedCard.setStatus("BLOCKED");

        when(cardService.putCardByUserId(1L, "BLOCKED"))
                .thenReturn(updatedCard);

        mockMvc.perform(put("/api/cards/1/status")
                        .param("status", "BLOCKED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }
}
