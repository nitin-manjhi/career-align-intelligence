package com.nit.controller;

import com.nit.entity.CityName;
import com.nit.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationResource {
    private final LocationService locationService;

    @GetMapping("/states")
    public List<String> getAllStates() {
        return locationService.getAllStates();
    }

    @GetMapping("/cities/{stateName}")
    public List<String> getCitiesByState(@PathVariable String stateName) {
        return locationService.getCitiesByStateName(stateName).stream()
                .map(CityName::getName)
                .toList();
    }
}

