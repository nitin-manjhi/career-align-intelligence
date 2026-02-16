package com.nit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "colleges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "state_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class CollegeName {
    @Id
    private Long id; // Use Long to match BIGINT

    @Column(nullable = false, length = 100)
    private String name;

    // Mapping the actual relationship instead of just the ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private StateName state;
}
