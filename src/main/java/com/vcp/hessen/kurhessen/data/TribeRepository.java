package com.vcp.hessen.kurhessen.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TribeRepository extends JpaRepository<Tribe, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u.tags FROM User u where u.tribe = :tribe")
    public Set<Set<String>> getAllUserTags(Tribe tribe);
}
