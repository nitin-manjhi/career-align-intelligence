package com.nit.repository;

import com.nit.entity.AnalysisResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResultEntity, UUID> {
}
