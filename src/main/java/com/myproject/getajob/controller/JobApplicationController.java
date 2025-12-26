package com.myproject.getajob.controller;

import com.myproject.getajob.dto.JobApplicationDto;
import com.myproject.getajob.entity.JobApplication;
import com.myproject.getajob.entity.User;
import com.myproject.getajob.entity.enums.ApplicationStatus;
import com.myproject.getajob.service.JobApplicationService;
import com.myproject.getajob.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService applicationService;
    private final UserService userService;

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<JobApplicationDto> applyForJob(
            @PathVariable Long jobId,
            @RequestParam(required = false) String coverLetter,
            @RequestParam(required = false) String manualExperience,
            @RequestParam(required = false) MultipartFile resume,
            Authentication authentication) {

        User user = userService.findByEmail(authentication.getName());
        JobApplication application = applicationService.applyForJob(jobId, user.getId(), coverLetter, manualExperience,
                resume);
        return ResponseEntity.ok(mapToDto(application));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<JobApplicationDto>> getMyApplications(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        List<JobApplication> applications = applicationService.getApplicationsByApplicant(user.getId());
        return ResponseEntity.ok(applications.stream().map(this::mapToDto).collect(Collectors.toList()));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<JobApplicationDto>> getJobApplicants(
            @PathVariable Long jobId,
            Authentication authentication) {

        User user = userService.findByEmail(authentication.getName());
        List<JobApplication> applications = applicationService.getApplicationsForJob(jobId, user.getId());
        return ResponseEntity.ok(applications.stream().map(this::mapToDto).collect(Collectors.toList()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobApplicationDto> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {

        User user = userService.findByEmail(authentication.getName());
        JobApplication application = applicationService.updateApplicationStatus(id, status, user.getId());
        return ResponseEntity.ok(mapToDto(application));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawApplication(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        applicationService.withdrawApplication(id, user.getId());
        return ResponseEntity.ok().build();
    }

    private JobApplicationDto mapToDto(JobApplication app) {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(app.getId());
        dto.setJobId(app.getJobListing().getId());
        dto.setJobTitle(app.getJobListing().getPositionTitle());
        dto.setCompanyName(app.getJobListing().getCompanyName());
        dto.setApplicantId(app.getApplicant().getId());
        dto.setApplicantName(app.getApplicant().getFirstName() + " " + app.getApplicant().getLastName());
        dto.setApplicantEmail(app.getApplicant().getEmail());
        dto.setResumeUrl(app.getResumeUrl());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setManualExperience(app.getManualExperience());
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());
        return dto;
    }
}
