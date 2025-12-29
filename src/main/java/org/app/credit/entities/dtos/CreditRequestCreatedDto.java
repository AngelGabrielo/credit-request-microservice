package org.app.credit.entities.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreditRequestCreatedDto(
        @NotNull
        @Min(value = 1000)
        @Max(value = 50000)
        BigDecimal amount,

        @NotNull
        @Min(value = 1)
        Integer periodDays,

        @NotBlank
        @Size(min = 20)
        String reason
) {

}
