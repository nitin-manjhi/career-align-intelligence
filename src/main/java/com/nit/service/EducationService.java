package com.nit.service;

import com.nit.entity.CollegeName;
import com.nit.entity.DegreeName;
import com.nit.repository.CollegeRepository;
import com.nit.repository.DegreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final DegreeRepository degreeRepository;
    private final CollegeRepository collegeRepository;

    public List<String> getAllDegrees() {
        return degreeRepository.findAll()
                .stream()
                .map(DegreeName::getName)
                .toList();
    }

    public List<String> getAllCollegesByState(String state) {
        return collegeRepository.findAll()
                .stream()
                .filter(college -> college.getState().getName().equalsIgnoreCase(state))
                .map(CollegeName::getName)
                .toList();
    }

}
