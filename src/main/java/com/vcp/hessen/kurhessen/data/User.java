package com.vcp.hessen.kurhessen.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
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
    private Boolean swimmingInGroupOfThreeAllowed;
    private Boolean moveFreelyInGroupOfThreeAllowed;



    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<EventParticipant> participants;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_emergency_contact_id")
    private UserEmergencyContact userEmergencyContact;

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
            return "missing Info";
        }

        if (this.level == null) {
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
            long daysSinceLevel = ageInDays - maxAge;
            if (daysSinceLevel < -365) {
                return "in " + daysSinceLevel/365 + " years";
            }
            if (daysSinceLevel < -31) {
                return "in " + daysSinceLevel/31 + " month";
            }
            return "in " + daysSinceLevel + " days";
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Integer getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Integer membershipId) {
        this.membershipId = membershipId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getIntolerances() {
        return intolerances;
    }

    public void setIntolerances(String intolerances) {
        this.intolerances = intolerances;
    }

    public String getEatingHabits() {
        return eatingHabits;
    }

    public void setEatingHabits(String eatingHabits) {
        this.eatingHabits = eatingHabits;
    }

    public Boolean getPicturesAllowed() {
        return picturesAllowed;
    }

    public void setPicturesAllowed(Boolean picturesAllowed) {
        this.picturesAllowed = picturesAllowed;
    }

    public Set<EventParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<EventParticipant> participants) {
        this.participants = participants;
    }
}
