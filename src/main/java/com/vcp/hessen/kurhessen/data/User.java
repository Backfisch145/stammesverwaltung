package com.vcp.hessen.kurhessen.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.features.events.data.EventParticipant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue()
    private Long id;

    @Version
    private int version;

    @Column
    private Integer membershipId;

    @Column
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
    private String address;

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
    private Set<Role> roles = new HashSet<>();

    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    private String intolerances;

    private String eatingHabits;

    @Column(columnDefinition = "false")
    private boolean picturesAllowed = false;
    @Column(columnDefinition = "false")
    private boolean swimmingInGroupOfThreeAllowed = false;
    @Column(columnDefinition = "false")
    private boolean moveFreelyInGroupOfThreeAllowed = false;



    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<EventParticipant> participants;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_emergency_contact_id")
    private UserEmergencyContact userEmergencyContact;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn
    private Tribe tribe;

    @Nullable
    public UserEmergencyContact getUserEmergencyContact() {
        return userEmergencyContact;
    }

    public void setUserEmergencyContact(UserEmergencyContact userEmergencyContact) {
        this.userEmergencyContact = userEmergencyContact;
    }

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
        this.intolerances = user.intolerances;
        this.eatingHabits = user.eatingHabits;
        this.picturesAllowed = user.picturesAllowed;
        this.participants = user.participants;
    }

    public User(Integer membershipId, String username, String firstName, String lastName, String email, String phone, String address, LocalDate dateOfBirth, Level level, Gender gender) {
        this.membershipId = membershipId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.level = level;
        this.gender = gender;
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    private long getDaysTillNextLevel() {
        if (this.dateOfBirth == null) {
            return -1;
        }

        if (this.level == null) {
            return -1;
        }

        return ChronoUnit.DAYS.between(this.dateOfBirth, LocalDateTime.now());
    }

    public String getUntilNextLevelString() {

        long ageInDays = getDaysTillNextLevel();
        if (ageInDays == -1) {
            return new TranslatableText("Unknown").translate();
        }

        if (this.level == null) {
            return "";
        }

        if (this.level == Level.ERWACHSEN) {
            return "";
        }

        long maxAge = this.level.getMaxAge() * 365L;
        if (maxAge < ageInDays) {
            long daysSinceLevel = ageInDays - maxAge;
            if (daysSinceLevel > 365) {
                return "vor " + daysSinceLevel/365 + " years";
            }
            if (daysSinceLevel > 31) {
                return "vor " + daysSinceLevel/31 + " month";
            }

            return "vor " + daysSinceLevel + " days";
        } else {
            long daysUntilLevel = maxAge - ageInDays;
            if (daysUntilLevel > 365) {
                return "in " + daysUntilLevel/365 + " years";
            }
            if (daysUntilLevel > 31) {
                return "in " + daysUntilLevel/31 + " month";
            }
            return "in " + daysUntilLevel + " days";
        }
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
}
