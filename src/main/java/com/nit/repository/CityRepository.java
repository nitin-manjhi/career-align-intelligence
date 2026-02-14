package com.nit.repository;

import com.nit.entity.CityName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<CityName, Long> {

    // Finds all cities belonging to a specific State ID
    List<CityName> findByStateId(Long stateId);

    // Finds cities by name (case-insensitive)
    List<CityName> findByNameContainingIgnoreCase(String name);
}