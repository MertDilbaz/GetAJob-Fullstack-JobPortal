package com.myproject.getajob.service;

import com.myproject.getajob.entity.Career;
import com.myproject.getajob.entity.User;
import com.myproject.getajob.repository.CareerRepository;
import com.myproject.getajob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CareerService {

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Career> getUserCareers(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return careerRepository.findByUserIdOrderByStartDateDesc(user.getId());
    }

    public Career addCareer(String email, Career career) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        career.setUser(user);
        return careerRepository.save(career);
    }

    public void deleteCareer(String email, Long careerId) {
        Objects.requireNonNull(careerId, "Career ID must not be null");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new RuntimeException("Career not found"));

        if (!Objects.equals(career.getUser().getId(), user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        careerRepository.delete(career);
    }
}
