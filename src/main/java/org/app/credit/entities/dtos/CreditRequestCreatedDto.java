package org.app.credit.entities.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreditRequestCreatedDto(
        @NotNull
        @Min(value = 1000)
        BigDecimal amount,

        @NotNull
        @Min(value = 1)
        Integer periodDays,

        @NotBlank
        @Size(min = 20)
        String reason
) {

}
