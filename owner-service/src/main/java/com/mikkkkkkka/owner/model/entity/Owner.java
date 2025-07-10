package com.mikkkkkkka.owner.model.entity;

import com.mikkkkkkka.common.model.dto.OwnerDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "owners", schema = "service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate birthday;

    public OwnerDto toDto() {
        return new OwnerDto(id, name, birthday);
    }
}