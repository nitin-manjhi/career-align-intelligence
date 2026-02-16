package com.nit.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "upgrade_requests")
public class UpgradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    String reason;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    RequestStatus status = RequestStatus.PENDING;

    @CreationTimestamp
    Instant createdAt;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
