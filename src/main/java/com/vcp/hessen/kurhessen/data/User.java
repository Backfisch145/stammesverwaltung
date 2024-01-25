package com.vcp.hessen.kurhessen.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.time.LocalDate;
import java.util.LinkedHashSet;
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
    @Column
    private int membershipId;
    @Column
    private String username;
    @Column
    private String firstName;
    @Column
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

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
            name = "users_intolerances",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "intolerances_name"))
    private Set<Intolerance> intolerances = new LinkedHashSet<>();

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
        this.intolerances = user.intolerances;
    }
}
