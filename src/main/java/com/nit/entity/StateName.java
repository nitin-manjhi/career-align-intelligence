package com.nit.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "states")
@Getter
@Setter
@NoArgsConstructor
public class StateName {
    @Id
    private Long id; // Use Long to match BIGINT

    @Column(unique = true, nullable = false)
    private String name;

    // Optional: Bidirectional mapping
    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<CityName> cities;

    // Optional: Bidirectional mapping
    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<CollegeName> collegeNames;
}
