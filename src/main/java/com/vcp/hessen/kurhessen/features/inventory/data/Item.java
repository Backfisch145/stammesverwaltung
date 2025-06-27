package com.vcp.hessen.kurhessen.features.inventory.data;

import com.vcp.hessen.kurhessen.data.Tribe;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column
    @Length(max = 255)
    private String name;

    @Length(max = 255)
    private String description;

    @Column
    private LocalDate createdAt;

    @Lob
    @ToString.Exclude
    @Column(length = 1000000)
    private byte[] picture;

    @ManyToOne
    @JoinColumn(name = "tribe_id")
    private Tribe tribe;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn
    private Item container;

    @OneToMany(mappedBy = "container", cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    @ToString.Exclude
    private Set<Item> items = new LinkedHashSet<>();

    private Set<String> tags = new LinkedHashSet<>();

    public Item(String name, String description, LocalDate createdAt, byte[] picture, Item inside) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.picture = picture;
        this.container = inside;
    }

    public Item(String name, String description, LocalDate createdAt) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    @Transactional
    public Set<Item> getItems() {
        Hibernate.initialize(this);
        return items;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Item item = (Item) o;
        return getId() != null && Objects.equals(getId(), item.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
