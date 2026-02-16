package com.nit.repository;

import com.nit.entity.StateName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<StateName, Long> {

    // Custom query to find a state by its exact name
    Optional<StateName> findByName(String name);
}