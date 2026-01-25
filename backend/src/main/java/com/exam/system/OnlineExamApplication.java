package com.exam.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class for Online Examination System
 * 
 * @author Final Year Project
 * @version 1.0.0
 */
@SpringBootApplication
public class OnlineExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineExamApplication.class, args);
        System.out.println("==============================================");
        System.out.println("Online Examination System Started Successfully");
        System.out.println("Server running on: http://localhost:8080/api");
        System.out.println("==============================================");
    }
}
