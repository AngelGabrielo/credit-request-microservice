package org.app.credit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.credit.entities.dtos.UserRegisterDto;
import org.app.credit.entities.dtos.UserResponseDto;
import org.app.credit.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Register: Debería retornar 201 Created cuando el usuario es válido")
    void register_ShouldReturn201_WhenValid() throws Exception {
        // ARRANGE
        UserRegisterDto registerDto = new UserRegisterDto("newuser", "securepass", false);

        // El servicio retorna un DTO de respuesta
        UserResponseDto responseDto = new UserResponseDto(1L, "newuser", true, List.of("ROLE_CLIENT"));
        when(userService.save(any(UserRegisterDto.class))).thenReturn(responseDto);

        // ACT & ASSERT
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated()) // Esperamos 201
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Register: Debería retornar 400 Bad Request si el password es muy corto")
    void register_ShouldReturn400_WhenPasswordIsShort() throws Exception {
        // ARRANGE
        // Password de 3 caracteres (tu DTO pide min 6)
        UserRegisterDto invalidDto = new UserRegisterDto("user", "123", false);

        // ACT & ASSERT
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        // Verificamos que NUNCA se llame al servicio
        verify(userService, never()).save(any());
    }

}