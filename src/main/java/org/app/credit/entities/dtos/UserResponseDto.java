package org.app.credit.entities.dtos;

import java.util.List;

public record UserResponseDto(
        Long id,
        String username,
        boolean enabled,
        List<String> roles
) {
}
