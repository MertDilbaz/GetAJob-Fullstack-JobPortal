package com.myproject.getajob.repository;

import com.myproject.getajob.entity.JobListing;
import com.myproject.getajob.entity.enums.JobStatus; // Status enum
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobListingRepository extends JpaRepository<JobListing, Long> {

    // Custom query to find approved jobs
    java.util.List<JobListing> findByStatus(JobStatus status);

    java.util.List<JobListing> findByPostedByUser_Id(Long userId);
}
