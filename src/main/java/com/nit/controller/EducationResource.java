package com.nit.controller;

import com.nit.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/education")
@RequiredArgsConstructor
public class EducationResource {
    private final EducationService educationService;

    @GetMapping("/degrees")
    public List<String> getAllDegrees() {
        return educationService.getAllDegrees();
    }

    @GetMapping("/collgeName/{stateName}")
    public List<String> getCollegeNameByState(@PathVariable String stateName) {
        return educationService.getAllCollegesByState(stateName);
    }
}
