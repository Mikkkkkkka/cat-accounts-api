package com.mikkkkkkka.common.model.dto;

import java.time.LocalDate;
import java.util.List;

public record OwnerDtoWithCats(
        Long id,
        String name,
        LocalDate birthday,
        List<Number> cats
) {
    public OwnerDtoWithCats(OwnerDto ownerNoCats, List<Number> cats) {
        this(ownerNoCats.id(),
                ownerNoCats.name(),
                ownerNoCats.birthday(),
                cats);
    }
}