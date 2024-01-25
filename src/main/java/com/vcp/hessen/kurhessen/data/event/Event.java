package com.vcp.hessen.kurhessen.data.event;

import com.vcp.hessen.kurhessen.data.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue()
    private Long id;
    @Version
    private int version;
    private String name;
    private String address;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;
    private LocalDateTime participationDeadline;
    private float price;

    @Formula("(" +
            "select count(*) " +
            "from event_participants ep " +
            "where ep.event_id = id" +
            " AND ep.status = 'REGISTERED'" +
            ")")
    private int participantCount;

    @OneToMany(mappedBy = "event", orphanRemoval = true)
    private Set<EventParticipant> participants = new LinkedHashSet<>();

}
