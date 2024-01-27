package com.vcp.hessen.kurhessen.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vcp.hessen.kurhessen.data.event.Event;
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
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Getter
    @Id
    @GeneratedValue()
    private Long id;
    @Getter
    @Version
    private int version;
    @Getter
    @Column
    private Integer membershipId;
    @Getter
    @Column
    private String username;
    @Getter
    @Column(nullable = false)
    private String firstName;
    @Getter
    @Column(nullable = false)
    private String lastName;
    @Getter
    @Column
    @Email
    private String email;
    @Getter
    @Column
    private String phone;
    @Getter
    @Column
    private LocalDate dateOfBirth;
    @Getter
    @Column
    @Enumerated(EnumType.STRING)
    private Level level;
    @Getter
    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Getter
    @JsonIgnore
    private String hashedPassword;
    @Getter
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Getter
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    private String intolerances;
    private String eatingHabits;

    @Getter
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

    public void setIntolerances(String intolerances) {
        this.intolerances = intolerances;
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

    public boolean isAdmin() {
        return this.roles.contains(Role.ADMIN);
    }
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
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
