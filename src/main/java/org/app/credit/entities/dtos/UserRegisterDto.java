package org.app.credit.entities.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDto(
        @NotBlank
        String username,

        @NotBlank
        @Size(min = 6)
        String password,

        boolean analyst
) {
}
