package com.nit.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String username;
    String password;
    String name;
    @Column(unique = true)
    String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    Role role = Role.USER;

    @Builder.Default
    Integer usageLimit = 2; // For standard analysis

    @Builder.Default
    Integer generationLimit = 2; // For resume generation

    @Builder.Default
    Integer analysisCount = 0;

    @Builder.Default
    Integer generationCount = 0;

    @Builder.Default
    boolean premiumActive = false;

    @Builder.Default
    Integer premiumUsageLimit = 0;

    @Builder.Default
    Integer premiumUsageCount = 0;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt; // soft delete

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
