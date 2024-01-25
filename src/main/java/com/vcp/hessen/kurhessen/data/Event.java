package com.vcp.hessen.kurhessen.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private float price;

    @ManyToMany
    @JoinTable(name = "events_users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id"))
    private Set<User> organisers = new LinkedHashSet<>();



}
