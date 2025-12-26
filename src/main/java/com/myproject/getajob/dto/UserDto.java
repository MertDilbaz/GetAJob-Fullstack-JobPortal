package com.myproject.getajob.dto;

import lombok.Data;
import java.time.LocalDateTime; // Explicitly used for createdAt field

@Data
public class UserDto {
    private Long id;
    private String email;
    private String role; // Simplified for display, or list of roles
    private LocalDateTime createdAt;
    private boolean enabled;

    // We intentionally don't include the real password for security.
    // Use a placeholder if UI demands it.
}
