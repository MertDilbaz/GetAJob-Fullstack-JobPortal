package com.myproject.getajob;

import org.springframework.boot.SpringApplication; // Spring Boot Main

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GetajobApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetajobApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner schemaFix(javax.sql.DataSource dataSource) {
        return args -> {
            try (java.sql.Connection conn = dataSource.getConnection();
                    java.sql.Statement stmt = conn.createStatement()) {
                // Fix for H2: allow nulls in company_id
                try {
                    stmt.execute("ALTER TABLE job_listings ALTER COLUMN company_id SET NULL");
                    System.out.println("PATCH: Altered job_listings.company_id to allow NULLs.");
                } catch (Exception e) {
                    // Ignore if already done or table doesn't exist yet
                    System.out.println("PATCH INFO: " + e.getMessage());
                }
            }
        };
    }

}
