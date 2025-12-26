package com.myproject.getajob.repository;

import com.myproject.getajob.entity.JobApplication;
import com.myproject.getajob.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByApplicantId(Long applicantId);

    List<JobApplication> findByJobListingId(Long jobListingId);

    boolean existsByApplicantIdAndJobListingId(Long applicantId, Long jobListingId);

    long countByApplicantIdAndStatus(Long applicantId, ApplicationStatus status);

    long countByApplicantId(Long applicantId);
}
