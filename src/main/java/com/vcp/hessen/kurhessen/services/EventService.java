package com.vcp.hessen.kurhessen.services;

import com.vcp.hessen.kurhessen.data.Role;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.event.Event;
import com.vcp.hessen.kurhessen.data.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.vcp.hessen.kurhessen.security.AuthenticatedUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final AuthenticatedUser authenticatedUser;

    private final EventRepository repository;

    public EventService(AuthenticatedUser authenticatedUser, EventRepository repository) {
        this.authenticatedUser = authenticatedUser;
        this.repository = repository;
    }

    public Optional<Event> get(Integer id) {
        return repository.findById(id);
    }

    public Event update(Event entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<Event> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Event> list(Pageable pageable, Specification<Event> filter) {
        if (authenticatedUser.get().isEmpty()) {
            return Page.empty();
        }

        if (!authenticatedUser.get().get().getRoles().contains(Role.ADMIN)) {
            List<Event> userEvents = repository.findEventsByParticipantsContainingUserId(authenticatedUser.get().get().getId());
            filter = filter.and((Specification<Event>) (root, query, criteriaBuilder) -> criteriaBuilder.and(criteriaBuilder.isTrue(root.in(userEvents))));
        }

        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
