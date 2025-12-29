package org.app.credit.entities.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginDto(
        @NotBlank
        String username,

        @NotBlank
        @Size(min = 6)
        String password
) {
}
