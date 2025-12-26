package com.myproject.getajob.service;

import com.myproject.getajob.dto.JobListingDto;
import com.myproject.getajob.entity.Company;
import com.myproject.getajob.entity.JobListing;
import com.myproject.getajob.entity.User;
import com.myproject.getajob.entity.enums.JobStatus; // Status enum
import com.myproject.getajob.repository.CompanyRepository;
import com.myproject.getajob.repository.JobListingRepository;
import com.myproject.getajob.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class JobService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JobService.class);

    private final JobListingRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;

    public JobService(JobListingRepository jobRepository, UserRepository userRepository,
            CompanyRepository companyRepository, NotificationService notificationService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public JobListingDto createJob(JobListingDto dto, String userEmail) {
        logger.info("Attempting to create job for user: {}", userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        logger.info("Found user for job creation: {} (ID: {})", user.getEmail(), user.getId());

        JobListing job = new JobListing();
        job.setPositionTitle(dto.getPositionTitle());
        job.setDescription(dto.getDescription());
        job.setCity(dto.getCity());
        job.setDistrict(dto.getDistrict());
        job.setFullAddress(dto.getFullAddress());
        job.setWorkplaceType(dto.getWorkplaceType());
        job.setEmploymentType(dto.getEmploymentType());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setCurrency(dto.getCurrency());
        job.setApplicationDeadline(dto.getApplicationDeadline());
        job.setRequirements(dto.getRequirements());
        job.setPostedByUser(user);

        // Handle Company: If ID provided use it, else use manual name
        if (dto.getCompanyId() != null) {
            Long companyId = dto.getCompanyId();
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            job.setCompany(company);
            job.setCompanyName(company.getCompanyName());
            job.setCompanyWebsite(company.getWebsite());
        } else {
            // Check if company name exists to avoid duplicates or create new?
            // For now, just save manual details
            job.setCompanyName(dto.getCompanyName());
            job.setCompanyWebsite(dto.getCompanyWebsite());
        }

        JobListing saved = jobRepository.save(job);
        logger.info("Job created with ID: {}, PostedByUserID: {}", saved.getId(), saved.getPostedByUser().getId());
        return mapToDto(saved);
    }

    public List<JobListingDto> getAllApprovedJobs() {
        return jobRepository.findByStatus(JobStatus.APPROVED).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<JobListingDto> getAllPendingJobs() {
        return jobRepository.findByStatus(JobStatus.PENDING).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<JobListingDto> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobListingDto> getJobsPostedByUser(String email) {
        logger.info("Fetching jobs for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<JobListing> jobs = jobRepository.findByPostedByUser_Id(user.getId());
        logger.info("Found {} jobs for user ID {}", jobs.size(), user.getId());

        return jobs.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobListingDto updateJobStatus(Long id, JobStatus status) {
        JobListing job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        JobListing savedJob = jobRepository.save(job);

        // Notify Job Owner if needed
        if (savedJob.getPostedByUser() != null) {
            String statusTr = status == JobStatus.APPROVED ? "ONAYLANDI"
                    : (status == JobStatus.REJECTED ? "REDDEDİLDİ" : "BEKLEMEDE");
            String emoji = status == JobStatus.APPROVED ? "✅" : (status == JobStatus.REJECTED ? "❌" : "⏳");

            String title = "İlan Durumu: " + statusTr + " " + emoji;
            String message = savedJob.getPositionTitle() + " başlıklı ilanınız güncellendi.";
            String type = status == JobStatus.APPROVED ? "SUCCESS" : (status == JobStatus.REJECTED ? "ERROR" : "INFO");

            notificationService.createNotification(savedJob.getPostedByUser(), title, message, type);
        }

        return mapToDto(savedJob);
    }

    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job not found");
        }
        jobRepository.deleteById(id);
    }

    @Transactional
    public void deleteJob(Long id, String userEmail) {
        JobListing job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check ownership
        if (job.getPostedByUser() == null || !job.getPostedByUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu ilanı silme yetkiniz yok.");
        }

        jobRepository.delete(job);
        logger.info("Job deleted by owner: {}", id);
    }

    private JobListingDto mapToDto(JobListing job) {
        JobListingDto dto = new JobListingDto();
        dto.setId(job.getId());
        dto.setPositionTitle(job.getPositionTitle());
        dto.setDescription(job.getDescription());
        dto.setCity(job.getCity());
        dto.setDistrict(job.getDistrict());
        dto.setFullAddress(job.getFullAddress());
        dto.setWorkplaceType(job.getWorkplaceType());
        dto.setEmploymentType(job.getEmploymentType());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setCurrency(job.getCurrency());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        dto.setRequirements(job.getRequirements());
        dto.setCompanyName(job.getCompanyName());
        dto.setCompanyWebsite(job.getCompanyWebsite());
        if (job.getCompany() != null) {
            dto.setCompanyId(job.getCompany().getId());
        }
        dto.setStatus(job.getStatus());
        dto.setCreatedAt(job.getCreatedAt());
        if (job.getPostedByUser() != null) {
            dto.setPostedByEmail(job.getPostedByUser().getEmail());
        }
        return dto;
    }
}
