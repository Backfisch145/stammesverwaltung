package com.vcp.hessen.kurhessen.features.events;

import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.features.events.data.Event;
import com.vcp.hessen.kurhessen.features.events.data.EventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventService {
    private final AuthenticatedUser authenticatedUser;

    private final EventRepository repository;

    public EventService(AuthenticatedUser authenticatedUser, EventRepository repository) {
        this.authenticatedUser = authenticatedUser;
        this.repository = repository;
    }

    public List<Event> getAll() {
        return repository.findAll();
    }
    public Optional<Event> get(Integer id) {
        return repository.findById(id);
    }
    @Transactional
    public Optional<Event> getComplete(Integer id) {
        Optional<Event> e = repository.findById(id);
        e.ifPresent(event -> Hibernate.initialize(event.getParticipants()));
        return e;
    }

    @Transactional
    public Event update(Event entity) {
        Event e = repository.save(entity);


        return e;
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }


    @PreAuthorize("hasAuthority('EVENT_READ')")
    public Page<Event> list(Pageable pageable) {
        return list(pageable, null);
    }
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public Page<Event> list(Pageable pageable, Specification<Event> filter) {
        User user = authenticatedUser.get().orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        List<Event> userEvents = repository.findEventsByParticipantsContainingUserId(user.getId());
//        Specification<Event> participantFilter = (root, query, criteriaBuilder) ->
//                criteriaBuilder.and(criteriaBuilder.isTrue(root.in(userEvents)));
//
//        if (filter != null) {
//            participantFilter = participantFilter.and(filter);
//        }

//        return repository.findAll(participantFilter, pageable);
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
