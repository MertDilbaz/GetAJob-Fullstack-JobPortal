package com.myproject.getajob.service;

import com.myproject.getajob.entity.JobApplication;
import com.myproject.getajob.entity.enums.ApplicationStatus;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface JobApplicationService {
    JobApplication applyForJob(Long jodId, Long userId, String coverLetter, String manualExperience,
            MultipartFile resume);

    List<JobApplication> getApplicationsByApplicant(Long userId);

    List<JobApplication> getApplicationsForJob(Long jobId, Long requesterId);

    JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status, Long requesterId);

    void withdrawApplication(Long applicationId, Long userId);
}
