package com.nit.repository;

import com.nit.entity.UpgradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpgradeRequestRepository extends JpaRepository<UpgradeRequest, Long> {
    List<UpgradeRequest> findAllByOrderByCreatedAtDesc();

    List<UpgradeRequest> findByStatusOrderByCreatedAtDesc(UpgradeRequest.RequestStatus status);

    void deleteByUserId(Long userId);
}
