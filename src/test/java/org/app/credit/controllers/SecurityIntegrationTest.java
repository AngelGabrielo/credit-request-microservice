package org.app.credit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.credit.entities.Role;
import org.app.credit.entities.User;
import org.app.credit.entities.dtos.UserLoginDto;
import org.app.credit.repositories.RoleRepository;
import org.app.credit.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        // Limpiamos y creamos un usuario de prueba en la BD H2/Test
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setEnabled(true);
        // Asegúrate de tener roles si tu lógica lo requiere
        Role role = roleRepository.findByName("ROLE_CLIENT").orElseThrow();
        user.setRoles(List.of(role));

        userRepository.save(user);
    }

    @Test
    @DisplayName("Login Exitoso: Debería retornar token en Header y Body")
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        // ARRANGE
        UserLoginDto loginDto = new UserLoginDto("testuser", "12345");

        // ACT & ASSERT
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("Login Fallido: Debería retornar 401 si la contraseña es incorrecta")
    void login_ShouldReturn401_WhenPasswordIsWrong() throws Exception {
        // ARRANGE
        UserLoginDto loginDto = new UserLoginDto("testuser", "wrongpassword");

        // ACT & ASSERT
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized()) // 401
                .andExpect(jsonPath("$.message").value("Authentication failed, wrong username or password"));
    }

    @Test
    @DisplayName("Ruta Protegida: Debería permitir acceso con Token válido")
    void protectedRoute_ShouldAllowAccess_WithValidToken() throws Exception {
        // 1. Primero hacemos login para obtener un token REAL
        UserLoginDto loginDto = new UserLoginDto("testuser", "12345");

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        // Extraemos el token del header (Formato: "Bearer xxxxx.yyyyy.zzzzz")
        String headerToken = result.getResponse().getHeader("Authorization");

        // 2. Intentamos acceder a una ruta protegida (ej. /requests/mine o cualquier otra)
        // Asegúrate de usar una ruta que exista en tus controllers
        mockMvc.perform(get("/requests/mine")
                        .header("Authorization", headerToken)) // Enviamos el token
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Ruta Protegida: Debería bloquear acceso sin Token (403/401)")
    void protectedRoute_ShouldDenyAccess_WithoutToken() throws Exception {
        mockMvc.perform(get("/requests/mine"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Ruta Protegida: Debería bloquear acceso con Token inválido")
    void protectedRoute_ShouldDenyAccess_WithBadToken() throws Exception {
        mockMvc.perform(get("/requests/mine")
                        .header("Authorization", "Bearer token_falso_12345"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid JWT token!"));
    }
}