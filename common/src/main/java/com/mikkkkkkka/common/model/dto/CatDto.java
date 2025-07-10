package com.mikkkkkkka.common.model.dto;

import com.mikkkkkkka.common.model.CatColor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record CatDto(
        Long id,
        String name,
        LocalDate birthday,
        String breed,
        CatColor color,
        Long ownerId,
        List<Long> friends
) {
}