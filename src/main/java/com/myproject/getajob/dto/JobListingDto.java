package com.myproject.getajob.dto;

import com.myproject.getajob.entity.enums.JobStatus; // Enum import
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobListingDto {
    private Long id;
    private String positionTitle;
    private String description;

    // Location
    private String city;
    private String district;
    private String fullAddress;

    // Details
    private String workplaceType;
    private String employmentType;
    private Double salaryMin;
    private Double salaryMax;
    private String currency;
    private LocalDate applicationDeadline;

    private List<String> requirements;

    // Company (Can be manual or linked)
    private Long companyId;
    private String companyName;
    private String companyWebsite;

    // Meta
    private JobStatus status;
    private LocalDateTime createdAt;

    private String postedByEmail; // Only for return, not input
}
