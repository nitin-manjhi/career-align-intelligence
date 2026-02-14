package com.nit.service;

import com.nit.entity.CityName;
import com.nit.entity.CollegeName;
import com.nit.entity.StateName;
import com.nit.repository.CityRepository;
import com.nit.repository.CollegeRepository;
import com.nit.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final CollegeRepository collegeRepository;

    public List<String> getAllStates() {
        List<StateName> stateNameList = stateRepository.findAll();
        return stateNameList.stream()
                .map(StateName::getName)
                .toList();
    }

    public List<CityName> getCitiesByStateName(String name) {
        StateName stateName = stateRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("State not found: " + name));
        return cityRepository.findByStateId(stateName.getId());
    }

    public List<CollegeName> getCollegesByStateName(String name) {
        StateName stateName = stateRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("State not found: " + name));
        return collegeRepository.findByStateId(stateName.getId());
    }
}
