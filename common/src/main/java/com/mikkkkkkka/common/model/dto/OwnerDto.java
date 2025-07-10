package com.mikkkkkkka.common.model.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record OwnerDto(
        Long id,
        String name,
        LocalDate birthday
) {
}
