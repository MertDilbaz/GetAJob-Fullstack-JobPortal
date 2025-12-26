package com.myproject.getajob.service;

import com.myproject.getajob.entity.JobApplication;
import com.myproject.getajob.entity.JobListing;
import com.myproject.getajob.entity.User;
import com.myproject.getajob.entity.enums.ApplicationStatus;
import com.myproject.getajob.repository.JobApplicationRepository;
import com.myproject.getajob.repository.JobListingRepository;
import com.myproject.getajob.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobListingRepository jobListingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    @SuppressWarnings("null")
    public JobApplication applyForJob(Long jobId, Long userId, String coverLetter, String manualExperience,
            org.springframework.web.multipart.MultipartFile resume) {
        Objects.requireNonNull(jobId, "Job ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");

        if (applicationRepository.existsByApplicantIdAndJobListingId(userId, jobId)) {
            throw new RuntimeException("Bu ilana zaten başvurdunuz.");
        }

        JobListing job = jobListingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("İlan bulunamadı"));

        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Prevent applying to own job
        if (job.getPostedByUser() != null && Objects.equals(job.getPostedByUser().getId(), userId)) {
            throw new RuntimeException("Kendi ilanınıza başvuramazsınız.");
        }

        JobApplication application = new JobApplication();
        application.setJobListing(job);
        application.setApplicant(applicant);
        application.setCoverLetter(coverLetter);
        application.setManualExperience(manualExperience);

        if (resume != null && !resume.isEmpty()) {
            try {
                // Simple file saving logic (local)
                String uploadDir = "uploads/resumes/";
                java.io.File directory = new java.io.File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + resume.getOriginalFilename();
                java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir + fileName);
                java.nio.file.Files.write(filePath, resume.getBytes());
                application.setResumeUrl("/resumes/" + fileName); // Servable path
            } catch (java.io.IOException e) {
                throw new RuntimeException("Resume upload failed", e);
            }
        }

        application.setStatus(ApplicationStatus.PENDING);
        JobApplication savedApplication = applicationRepository.save(application);

        // Notify Employer
        if (job.getPostedByUser() != null) {
            String title = "Yeni Başvuru 📬";
            String message = job.getPositionTitle() + " ilanı için yeni bir aday başvurdu.";
            notificationService.createNotification(job.getPostedByUser(), title, message, "INFO");
        }

        return savedApplication;
    }

    @Override
    @SuppressWarnings("null")
    public List<JobApplication> getApplicationsByApplicant(Long userId) {
        Objects.requireNonNull(userId, "User ID must not be null");
        return applicationRepository.findByApplicantId(userId);
    }

    @Override
    @SuppressWarnings("null")
    public List<JobApplication> getApplicationsForJob(Long jobId, Long requesterId) {
        Objects.requireNonNull(jobId, "Job ID must not be null");
        Objects.requireNonNull(requesterId, "Requester ID must not be null");

        JobListing job = jobListingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("İlan bulunamadı"));

        // Security check: only the job owner can see applicants
        if (job.getPostedByUser() == null || !Objects.equals(job.getPostedByUser().getId(), requesterId)) {
            // Check if admin? For now, stick to job owner requirement.
            throw new RuntimeException("Yetkisiz işlem: Bu ilan size ait değil.");
        }

        return applicationRepository.findByJobListingId(jobId);
    }

    @Override
    @Transactional
    public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status, Long requesterId) {
        Objects.requireNonNull(applicationId, "Application ID must not be null");
        Objects.requireNonNull(requesterId, "Requester ID must not be null");

        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı"));

        JobListing job = application.getJobListing();
        // Security check
        if (job.getPostedByUser() == null || !Objects.equals(job.getPostedByUser().getId(), requesterId)) {
            throw new RuntimeException("Yetkisiz işlem: Bu ilan size ait değil.");
        }

        application.setStatus(status);
        JobApplication savedApplication = applicationRepository.save(application);

        // Notify Applicant
        String statusTr = status == ApplicationStatus.ACCEPTED ? "KABUL EDİLDİ"
                : (status == ApplicationStatus.REJECTED ? "REDDEDİLDİ" : "DEĞERLENDİRİLİYOR");
        String emoji = status == ApplicationStatus.ACCEPTED ? "🎉"
                : (status == ApplicationStatus.REJECTED ? "😔" : "📋");

        String title = "Başvuru Sonucu: " + statusTr + " " + emoji;
        String message = job.getPositionTitle() + " pozisyonu için başvurunuz güncellendi.";
        String type = status == ApplicationStatus.ACCEPTED ? "SUCCESS"
                : (status == ApplicationStatus.REJECTED ? "ERROR" : "INFO");

        notificationService.createNotification(application.getApplicant(), title, message, type);

        return savedApplication;
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, Long userId) {
        Objects.requireNonNull(applicationId, "Application ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");

        System.out.println("Attempting to withdraw application: " + applicationId + " for user: " + userId);

        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı."));

        if (!Objects.equals(application.getApplicant().getId(), userId)) {
            System.err.println("Unauthorized withdrawal attempt. AppOwner: " + application.getApplicant().getId()
                    + ", Requester: " + userId);
            throw new RuntimeException("Bu başvuruyu kaldırma yetkiniz yok.");
        }

        // Delete resume file if exists
        try {
            if (application.getResumeUrl() != null && !application.getResumeUrl().isEmpty()) {
                // ResumeURL is like /uploads/resumes/filename.pdf
                // We need to convert it to actual file path.
                // Assuming it's stored in project root / uploads / resumes
                // Note: ResumeURL might be relative or full URL depending on how I implemented
                // it.
                // In applyForJob I did: String fileUrl = "/uploads/resumes/" + fileName;

                // Creating a file object
                java.io.File file = new java.io.File("." + application.getResumeUrl());
                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("Deleted resume file: " + file.getAbsolutePath());
                    } else {
                        System.err.println("Failed to delete resume file: " + file.getAbsolutePath());
                    }
                } else {
                    // Try absolute path if relative fails, though "." + url should work if running
                    // from root
                    java.io.File absFile = new java.io.File(
                            "c:\\Users\\mertd\\IdeaProjects\\getajob" + application.getResumeUrl().replace("/", "\\"));
                    if (absFile.exists()) {
                        if (absFile.delete()) {
                            System.out.println("Deleted resume file (abs): " + absFile.getAbsolutePath());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Log but don't fail the transaction
            System.err.println("Error deleting resume file: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            applicationRepository.delete(application);
            System.out.println("Application deleted from DB: " + applicationId);
        } catch (Exception e) {
            System.err.println("FATAL ERROR deleting application " + applicationId);
            e.printStackTrace();
            throw new RuntimeException("Başvuru silinirken veritabanı hatası oluştu: " + e.getMessage());
        }
    }
}
