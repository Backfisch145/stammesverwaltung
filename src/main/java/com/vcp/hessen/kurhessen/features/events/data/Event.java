package com.vcp.hessen.kurhessen.features.events.data;

import com.vcp.hessen.kurhessen.core.security.Role;
import com.vcp.hessen.kurhessen.data.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import org.hibernate.proxy.HibernateProxy;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
@Table(name = "events")
@ToString
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue
    private Integer id;
    @Version
    private int version;
    @NotBlank(message = "The name of the Event must not be Blank")
    private String name;
    private String address;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;
    private LocalDateTime participationDeadline;
    private LocalDateTime paymentDeadline;
    private Double price;

    @Formula("(" +
            "select count(*) " +
            "from event_participants ep " +
            "where ep.event_id = id" +
            " AND ep.status = 'REGISTERED'" +
            ")")
    @Transient
    private int participantCount;



    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<EventParticipant> participants = new LinkedHashSet<>();

    public boolean isUserParticipant(User user) {
        return this.getParticipants().stream()
                .map(EventParticipant::getUser)
                .anyMatch(us -> us.getId().equals(user.getId()));
    }

    public boolean isUserAllowedToSee(User user) {
        return this.getParticipants().stream()
                .map(EventParticipant::getUser)
                .anyMatch(us -> us.getId().equals(user.getId()));
    }
    @Nullable
    public EventParticipant getEventParticipation(User user) {
        return this.getParticipants().stream()
                .filter(us -> us.getUser().getId().equals(user.getId()))
                .findFirst().orElse(null);
    }

    public void incVersion() {
        version++;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
