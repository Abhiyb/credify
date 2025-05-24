package com.zeta.backend.controller;

import com.zeta.backend.service.CardLimitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardLimitController.class)
public class CardLimitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardLimitService cardLimitService;

    @Test
    void testUpdateCreditLimit() throws Exception {
        when(cardLimitService.updateCreditLimit(1L,200000.0)).thenReturn("Credit limit changed");

        mockMvc.perform(put("/cards/1/limit")
                        .param("newLimit", "200000.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Credit limit changed"));
    }
}