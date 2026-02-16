package com.nit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "degrees")
@Getter
@Setter
@NoArgsConstructor
public class DegreeName {
    @Id
    private Long id; // Use Long to match BIGINT

    @Column(unique = true, nullable = false)
    private String name;
}
