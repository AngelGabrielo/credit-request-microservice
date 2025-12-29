package org.app.credit.entities.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreditRequestCreatedDto(
        @NotNull(message = "The amount is required")
        @Min(value = 1000, message = "The min amount is 1000")
        BigDecimal amount,

        @NotNull(message = "The period is required")
        @Min(value = 1, message = "The min period is 1 day")
        Integer periodDays,

        @NotBlank(message = "The reason is required")
        @Size(min = 20, message = "The reason must have at least 20 characters")
        String reason
) {

}
