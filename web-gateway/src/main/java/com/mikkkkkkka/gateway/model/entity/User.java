package com.mikkkkkkka.gateway.model.entity;

import com.mikkkkkkka.common.model.UserRole;
import com.mikkkkkkka.common.model.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", schema = "service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(unique = true)
    private Long ownerId;

    public UserDto toDto() {
        return new UserDto(id,
                username,
                password,
                role,
                ownerId);
    }
}
