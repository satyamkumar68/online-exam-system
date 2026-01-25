package com.exam.system.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
