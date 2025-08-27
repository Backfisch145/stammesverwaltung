package com.vcp.hessen.kurhessen.data;

import com.vcp.hessen.kurhessen.features.events.data.Event;
import com.vcp.hessen.kurhessen.features.inventory.data.Item;
import jakarta.persistence.*;
import lombok.*;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

    @Column
    @Nullable
    TribeMembership tribeMembership = null;

    @ToString.Exclude
    @OneToMany(mappedBy = "tribe", orphanRemoval = true)
    Set<User> users = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "tribe", orphanRemoval = true)
    private Set<UserTag> userTags = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "tribe", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private Set<Item> items = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "tribe", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private Set<Event> events = new LinkedHashSet<>();

    public Tribe(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tribe tribe = (Tribe) o;
        return id == tribe.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
