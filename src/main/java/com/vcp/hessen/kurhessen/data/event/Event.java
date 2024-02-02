package com.vcp.hessen.kurhessen.data.event;

import com.vcp.hessen.kurhessen.data.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "events")
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
    private int participantCount;

    @OneToMany(mappedBy = "event", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<EventParticipant> participants = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public LocalDateTime getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(LocalDateTime endingTime) {
        this.endingTime = endingTime;
    }

    public LocalDateTime getParticipationDeadline() {
        return participationDeadline;
    }

    public void setParticipationDeadline(LocalDateTime participationDeadline) {
        this.participationDeadline = participationDeadline;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public Set<EventParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<EventParticipant> participants) {
        this.participants = participants;
    }

    public LocalDateTime getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(LocalDateTime paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public boolean isUserParticipant(User user) {
        return this.getParticipants().stream()
                .map(EventParticipant::getUser)
                .anyMatch(us -> us.getId().equals(user.getId()));
    }
    @Nullable
    public EventParticipant getEventParticipation(User user) {
        return this.getParticipants().stream()
                .filter(us -> us.getUser().getId().equals(user.getId()))
                .findFirst().orElseGet(null);
    }
}
