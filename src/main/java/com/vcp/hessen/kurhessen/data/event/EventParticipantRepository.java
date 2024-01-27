package com.vcp.hessen.kurhessen.data.event;


import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EventParticipantRepository
        extends
            JpaRepository<EventParticipant, Long>,
            JpaSpecificationExecutor<EventParticipant> {
}
