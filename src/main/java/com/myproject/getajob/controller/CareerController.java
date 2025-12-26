package com.myproject.getajob.controller;

import com.myproject.getajob.entity.Career;
import com.myproject.getajob.service.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/career")
public class CareerController {

    @Autowired
    private CareerService careerService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Career>> getMyCareers(Authentication authentication) {
        return ResponseEntity.ok(careerService.getUserCareers(authentication.getName()));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Career> addCareer(@RequestBody Career career, Authentication authentication) {
        return ResponseEntity.ok(careerService.addCareer(authentication.getName(), career));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCareer(@PathVariable Long id, Authentication authentication) {
        careerService.deleteCareer(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }
}
