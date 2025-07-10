package com.mikkkkkkka.common.model.dto;

import com.mikkkkkkka.common.model.UserRole;

public record UserDto(
        Long id,
        String username,
        String password,
        UserRole role,
        Long ownerId
) {
}
