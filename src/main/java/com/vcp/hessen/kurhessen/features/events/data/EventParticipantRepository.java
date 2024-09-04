package com.vcp.hessen.kurhessen.features.events.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventParticipantRepository
        extends
            JpaRepository<EventParticipant, Long>,
            JpaSpecificationExecutor<EventParticipant> {
}
