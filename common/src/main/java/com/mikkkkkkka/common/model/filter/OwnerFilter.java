package com.mikkkkkkka.common.model.filter;

import java.time.LocalDate;

public record OwnerFilter(
        LocalDate birthdayAfter,
        LocalDate birthdayBefore
) {
}