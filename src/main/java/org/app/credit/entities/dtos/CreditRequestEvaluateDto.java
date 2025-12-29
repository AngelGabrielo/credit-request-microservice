package org.app.credit.entities.dtos;

import jakarta.validation.constraints.NotNull;
import org.app.credit.entities.enums.RequestStateEnum;

public record CreditRequestEvaluateDto(
        @NotNull
        RequestStateEnum state
) {}
