package com.myproject.getajob.repository;

import com.myproject.getajob.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findByUserIdOrderByStartDateDesc(Long userId);
}
