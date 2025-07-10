package com.mikkkkkkka.cat.model.entity;

import com.mikkkkkkka.common.model.CatColor;
import com.mikkkkkkka.common.model.dto.CatDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cats", schema = "service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate birthday;

    private String breed;

    @Enumerated(EnumType.STRING)
    private CatColor color;

    @Column(name = "owner_id")
    private Long ownerId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "friendships", schema = "service",
            joinColumns = @JoinColumn(name = "friender_id"),
            inverseJoinColumns = @JoinColumn(name = "friendee_id"))
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    private List<Cat> friends;

    static String getStringCatArray(List<Cat> cats) {
        if (cats == null) return "[]";
        StringBuilder builder = new StringBuilder(cats.getClass().getSimpleName());
        if (cats.isEmpty()) return builder.append("[]").toString();
        builder.append("[");
        cats.forEach(cat -> builder.append(String.format("Cat(id=%d)", cat.getId())).append(", "));
        builder.replace(builder.length() - 2, builder.length(), "]");
        return builder.toString();
    }

    @ToString.Include(name = "friends")
    String formatFriends() {
        return getStringCatArray(friends);
    }

    public CatDto toDto() {
        return new CatDto(id,
                name,
                birthday,
                breed,
                color,
                ownerId,
                friends.stream()
                        .map(Cat::getId)
                        .toList());
    }
}