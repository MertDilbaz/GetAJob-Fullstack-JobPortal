package com.myproject.getajob.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String website;

    @Column(length = 1000)
    private String about;

    @OneToMany(mappedBy = "company")
    private List<JobListing> jobListings;
}
