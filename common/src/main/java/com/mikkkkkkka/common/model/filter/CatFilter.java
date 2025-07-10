package com.mikkkkkkka.common.model.filter;

import com.mikkkkkkka.common.model.CatColor;

import java.time.LocalDate;
import java.util.List;

public record CatFilter(
        Long ownerId,
        List<CatColor> colors,
        LocalDate birthdateAfter,
        LocalDate birthdateBefore
) {
}