package com.myproject.getajob.controller;

import com.myproject.getajob.entity.Notification;
import com.myproject.getajob.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(notificationService.getUserNotifications(email));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(notificationService.getUnreadCount(email));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        String email = getCurrentUserEmail();
        notificationService.markAllAsRead(email);
        return ResponseEntity.ok().build();
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
