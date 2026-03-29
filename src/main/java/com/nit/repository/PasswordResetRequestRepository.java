package com.nit.repository;

import com.nit.entity.PasswordResetRequest;
import com.nit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {
    
    Optional<PasswordResetRequest> findByUser(User user);
    
    void deleteByUser(User user);
}
