package com.myproject.getajob.dto;

import com.myproject.getajob.entity.enums.ApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail; // useful for contact
    private String resumeUrl;
    private String coverLetter;
    private String manualExperience;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
