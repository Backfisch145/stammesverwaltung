package com.vcp.hessen.kurhessen.data.event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EventRepository
        extends
            JpaRepository<Event, Integer>,
            JpaSpecificationExecutor<Event> {

    @Transactional
    @Query("select e from Event e " +
            " join EventParticipant p on e.id = p.event.id " +
            "  where p in (" +
            "   select p2 " +
            "   From EventParticipant p2" +
            "   where p2.user.id = ?1"+
            "  )")
    List<Event> findEventsByParticipantsContainingUserId(Long userId);

}
