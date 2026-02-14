package com.nit.repository;

import com.nit.entity.DegreeName;
import com.nit.entity.StateName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DegreeRepository extends JpaRepository<DegreeName, Long> {

}