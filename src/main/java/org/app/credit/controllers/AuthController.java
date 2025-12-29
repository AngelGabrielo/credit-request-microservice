package org.app.credit.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.app.credit.entities.dtos.UserLoginDto;
import org.app.credit.entities.dtos.UserRegisterDto;
import org.app.credit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<?> create(@Valid @RequestBody UserRegisterDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @Operation(
            summary = "Iniciar Sesión",
            description = "Autentica al usuario y devuelve un token JWT. (Este endpoint es interceptado por el filtro de seguridad)."
    )
    @PostMapping("/login")
    public void login(@RequestBody UserLoginDto userLoginDto) {
        throw new IllegalStateException("Security filter should intercept this method.");
    }

}
