package com.myproject.getajob.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
}
