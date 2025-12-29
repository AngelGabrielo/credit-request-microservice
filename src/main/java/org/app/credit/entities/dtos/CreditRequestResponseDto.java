package org.app.credit.entities.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditRequestResponseDto(
        Long id,
        BigDecimal amount,
        Integer periodDays,
        String reason,
        String state,
        LocalDateTime date
) {
}
