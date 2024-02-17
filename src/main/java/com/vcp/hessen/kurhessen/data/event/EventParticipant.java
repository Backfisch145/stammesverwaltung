package com.vcp.hessen.kurhessen.data.event;

import com.vcp.hessen.kurhessen.data.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Slf4j
@Getter
@Entity
@Table(name = "event_participants")
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipant {

    @Id
    @GeneratedValue()
    private Long id;
    @Enumerated(EnumType.STRING)
    private EventParticipationStatus status;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EventRole eventRole;

    @NotNull
    @OneToOne
    private User user;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;


    public EventParticipant(EventParticipationStatus status, EventRole eventRole, User user, Event event) {
        this.status = status;
        this.eventRole = eventRole;
        this.user = user;
        this.event = event;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(EventParticipationStatus status) {
        this.status = status;
    }

    public void setEventRole(EventRole eventRole) {
        this.eventRole = eventRole;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            log.info("1");
            return true;
        };
        if (o == null) {
            log.info("2");
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            log.info("3");
            return false;
        }
        EventParticipant that = (EventParticipant) o;
        if (getId() != null && Objects.equals(getId(), that.getId())) {
            log.info("4");
            return true;
        }

        return false;
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
