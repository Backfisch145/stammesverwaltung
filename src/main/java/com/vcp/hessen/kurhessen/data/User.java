package com.vcp.hessen.kurhessen.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;
import com.vcp.hessen.kurhessen.core.security.Permission;
import com.vcp.hessen.kurhessen.core.security.Role;
import com.vcp.hessen.kurhessen.features.events.data.EventParticipant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private int version;

    @Column(unique = true, nullable = true)
    @Nullable
    private Integer membershipId;

    @Column
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    @Email
    @Nullable
    private String email;

    @Column
    @Nullable
    private String phone;

    @Column
    private LocalDate dateOfBirth;

    @Column
    private LocalDate joinDate;

    @Column
    private String address;

    @Column
    @Enumerated(EnumType.STRING)
    private Level level;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNKNOWN;

    @JsonIgnore
    private String hashedPassword;

    @ToString.Exclude
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] profilePicture;

    private String intolerances;

    private String eatingHabits;

    @Column(columnDefinition = "false")
    private boolean picturesAllowed = false;
    @Column(columnDefinition = "false")
    private boolean canSwim = false;

    private LocalDate infoUpdateMailSent = null;


    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<EventParticipant> participants;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_emergency_contact_id")
    private UserEmergencyContact userEmergencyContact;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn
    private Tribe tribe;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    @JoinColumn
    private Set<UserFile> userFiles = new LinkedHashSet<>();

    private Set<String> tags =  new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_name"))
    private Set<Role> roles = new LinkedHashSet<>();


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
        this.joinDate = user.joinDate;
        this.level = user.level;
        this.gender = user.gender;
        this.roles = user.roles;
        this.intolerances = user.intolerances;
        this.eatingHabits = user.eatingHabits;
        this.picturesAllowed = user.picturesAllowed;
        this.participants = user.participants;
        this.infoUpdateMailSent = user.infoUpdateMailSent;
    }

    public User(Integer membershipId, String username, String firstName, String lastName, String email, String phone, String address, LocalDate dateOfBirth, LocalDate joinDate, Level level, Gender gender) {
        this.membershipId = membershipId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.joinDate = joinDate;
        this.level = level;
        this.gender = gender;
        this.infoUpdateMailSent = LocalDate.now();
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    public boolean hasPermission(String permissionStr) {
        for (GrantedAuthority authority : getAuthorities()) {
           if (permissionStr.equals(authority.getAuthority()) || permissionStr.equals(authority.getAuthority().replace("ROLE_", ""))) {
               return true;
           }
        }
        return false;
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

        if (this.level == null) {
            return new TranslatableText("Unknown").translate();
        }

        long ageInDays = getDaysTillNextLevel();
        if (ageInDays == -1) {
            return new TranslatableText("Unknown").translate();
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

    public boolean hasMembershipContract() {
        return userFiles.stream().anyMatch(it -> it.getType() == UserFile.UserFileType.MEMBERSHIP_AGREEMENT);
    }

    public List<GrantedAuthority> getAuthorities() {
        Set<String> permissions = new HashSet<>();

        for (Role role : this.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                permissions.add("ROLE_" + permission.name());
            }
        }

        return permissions.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
