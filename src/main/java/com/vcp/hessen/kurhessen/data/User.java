package com.vcp.hessen.kurhessen.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue()
    private Long id;
    @Version
    private int version;
    @Column(nullable = true)
    private Integer membershipId;
    @Column(nullable = true)
    private String username;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column
    @Email
    private String email;
    @Column
    private String phone;
    @Column
    private LocalDate dateOfBirth;
    @Column
    @Enumerated(EnumType.STRING)
    private Level level;
    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @JsonIgnore
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    private String intolerances;
    private String eatingHabits;

    private Boolean picturesAllowed;

    public String getDisplayName() {
       return firstName + " " + lastName;
   }

    public User(User user) {
        this.id = user.id;
        this.version = user.version;
        this.username = user.username;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.email = user.email;
        this.phone = user.phone;
        this.dateOfBirth = user.dateOfBirth;
        this.level = user.level;
        this.gender = user.gender;
        this.roles = user.roles;
        this.profilePicture = user.profilePicture;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public String getIntolerances() {
        if (intolerances == null) {
            return "";
        }
        return intolerances;
    }

    public String getEatingHabits() {
        if (eatingHabits == null) {
            return "";
        }
        return eatingHabits;
    }
}
