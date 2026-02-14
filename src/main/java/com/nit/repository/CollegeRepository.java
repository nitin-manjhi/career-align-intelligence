package com.nit.repository;

import com.nit.entity.CollegeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollegeRepository extends JpaRepository<CollegeName, Long> {
    List<CollegeName> findByStateId(Long stateId);
}

