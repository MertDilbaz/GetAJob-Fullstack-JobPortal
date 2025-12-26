package com.myproject.getajob.controller;

import com.myproject.getajob.dto.JobListingDto;
import com.myproject.getajob.entity.enums.JobStatus;
import com.myproject.getajob.service.JobService;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobListingDto> createJob(@RequestBody JobListingDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        JobListingDto created = jobService.createJob(dto, email);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<JobListingDto>> getAllApprovedJobs() {
        return ResponseEntity.ok(jobService.getAllApprovedJobs());
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<List<JobListingDto>> getMyJobs() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(jobService.getJobsPostedByUser(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyJob(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        jobService.deleteJob(id, email);
        return ResponseEntity.noContent().build();
    }

    // Admin endpoints
    @GetMapping("/admin/pending")
    // @PreAuthorize("hasRole('ADMIN')") // SecurityConfig handles this or
    // annotation
    public ResponseEntity<List<JobListingDto>> getPendingJobs() {
        return ResponseEntity.ok(jobService.getAllPendingJobs());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<JobListingDto>> getAllJobsForAdmin() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<JobListingDto> updateJobStatus(@PathVariable Long id, @RequestParam JobStatus status) {
        return ResponseEntity.ok(jobService.updateJobStatus(id, status));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}
