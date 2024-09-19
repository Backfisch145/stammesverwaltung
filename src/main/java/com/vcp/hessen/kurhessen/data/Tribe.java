package com.vcp.hessen.kurhessen.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tribe")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Tribe {
    @Id
    @Column(name = "id", nullable = false)
    long id;

    @Column(name = "name", nullable = false)
    String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "tribe", orphanRemoval = true)
    Set<User> users = new HashSet<>();

    public Tribe(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
