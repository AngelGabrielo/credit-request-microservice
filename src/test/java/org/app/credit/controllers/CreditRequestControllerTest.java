package org.app.credit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.credit.entities.dtos.CreditRequestCreatedDto;
import org.app.credit.entities.dtos.CreditRequestEvaluateDto;
import org.app.credit.entities.dtos.CreditRequestResponseDto;
import org.app.credit.entities.enums.RequestStateEnum;
import org.app.credit.services.CreditRequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CreditRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreditRequestService creditRequestService;

    @Test
    @DisplayName("POST /requests: Debería retornar 200 OK y DTO cuando el request es válido")
    void save_ShouldReturn200_WhenRequestIsValid() throws Exception {
        // ARRANGE
        CreditRequestCreatedDto requestDto = new CreditRequestCreatedDto(
                new BigDecimal("5000"), 30, "Quiero comprar una laptop"
        );

        CreditRequestResponseDto responseDto = new CreditRequestResponseDto(
                1L, new BigDecimal("5000"), 30, "Quiero comprar una laptop", "PENDING", LocalDateTime.now()
        );

        when(creditRequestService.save(any(CreditRequestCreatedDto.class))).thenReturn(responseDto);

        // ACT & ASSERT
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(5000))
                .andExpect(jsonPath("$.state").value("PENDING"));
    }

    @Test
    @DisplayName("POST /requests: Debería retornar 400 Bad Request cuando el monto excede 50,000")
    void save_ShouldReturn400_WhenAmountExceedsMax() throws Exception {
        // ARRANGE
        // Creamos un DTO inválido (50001) para probar tu @Max(50000)
        CreditRequestCreatedDto invalidDto = new CreditRequestCreatedDto(
                new BigDecimal("50001"), 30, "Quiero comprar una mansión"
        );

        // ACT & ASSERT
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        // Verificamos que NUNCA se llamó al servicio porque falló antes (en validación)
        verify(creditRequestService, never()).save(any());
    }

    @Test
    @DisplayName("GET /requests/mine: Debería retornar lista de mis solicitudes")
    void findMine_ShouldReturnList() throws Exception {
        // ARRANGE
        List<CreditRequestResponseDto> list = List.of(
                new CreditRequestResponseDto(1L, BigDecimal.TEN, 10, "R1", "PENDING", null)
        );

        when(creditRequestService.findByUsername()).thenReturn(list);

        // ACT & ASSERT
        mockMvc.perform(get("/requests/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1)) // Esperamos 1 elemento
                .andExpect(jsonPath("$[0].reason").value("R1"));

        verify(creditRequestService).findByUsername();
    }

    @Test
    @DisplayName("PUT /evaluate/{id}: Debería actualizar y retornar 200")
    void update_ShouldReturn200_WhenValid() throws Exception {
        // ARRANGE
        Long id = 1L;
        CreditRequestEvaluateDto evaluateDto = new CreditRequestEvaluateDto(RequestStateEnum.APPROVED);
        CreditRequestResponseDto responseDto = new CreditRequestResponseDto(
                id, BigDecimal.TEN, 10, "R1", "APPROVED", null
        );

        when(creditRequestService.update(eq(id), any(CreditRequestEvaluateDto.class))).thenReturn(responseDto);

        // ACT & ASSERT
        mockMvc.perform(put("/requests/evaluate/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evaluateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("APPROVED"));
    }


}