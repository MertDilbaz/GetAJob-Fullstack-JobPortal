package com.myproject.getajob.entity;

import com.myproject.getajob.entity.enums.JobStatus; // Status enum
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "job_listings")
public class JobListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String positionTitle;

    @Column(length = 5000)
    private String description;

    private String city;
    private String district;
    private String fullAddress;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.PENDING;

    private String workplaceType; // Remote, Hybrid, Onsite
    private String employmentType; // Full-time, Javascript, etc.

    private Double salaryMin;
    private Double salaryMax;
    private String currency; // TRY, USD, etc.

    private java.time.LocalDate applicationDeadline;

    @ElementCollection
    @CollectionTable(name = "job_requirements", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "requirement")
    private java.util.List<String> requirements = new java.util.ArrayList<>();

    // Audit fields
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (status == null)
            status = JobStatus.PENDING;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true) // Can be null if manual entry
    private Company company;

    // Helper for manual company name if company entity not used
    private String companyName;
    private String companyWebsite;

    @OneToMany(mappedBy = "jobListing", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<JobApplication> applications = new java.util.ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_user_id")
    private User postedByUser;

}
