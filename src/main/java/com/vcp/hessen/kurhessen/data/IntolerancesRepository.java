package com.vcp.hessen.kurhessen.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IntolerancesRepository extends JpaRepository<Intolerance, Long>, JpaSpecificationExecutor<Intolerance> {

    Intolerance findByName(String name);
}
