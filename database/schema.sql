-- ============================================
-- Online Examination System - Database Schema
-- ============================================

-- Drop existing database if exists
DROP DATABASE IF EXISTS online_exam_system;

-- Create database
CREATE DATABASE online_exam_system;
USE online_exam_system;

-- ============================================
-- Table: users
-- Description: Stores user information for both Admin and Students
-- ============================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table: exams
-- Description: Stores exam details created by admin
-- ============================================
CREATE TABLE exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_title VARCHAR(200) NOT NULL,
    exam_description TEXT,
    duration_minutes INT NOT NULL,
    total_marks INT NOT NULL,
    passing_marks INT NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    start_time DATETIME,
    end_time DATETIME,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_exam_title (exam_title),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table: questions
-- Description: Stores questions for each exam
-- ============================================
CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MCQ', 'TRUE_FALSE') DEFAULT 'MCQ',
    marks INT NOT NULL DEFAULT 1,
    correct_option CHAR(1) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table: options
-- Description: Stores options for MCQ questions
-- ============================================
CREATE TABLE options (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    option_label CHAR(1) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table: exam_attempts
-- Description: Tracks student exam attempts
-- ============================================
CREATE TABLE exam_attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    user_id INT NOT NULL,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    score DECIMAL(5,2) DEFAULT 0,
    status ENUM('IN_PROGRESS', 'COMPLETED', 'AUTO_SUBMITTED') DEFAULT 'IN_PROGRESS',
    is_proctored BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_exam (user_id, exam_id),
    INDEX idx_user_id (user_id),
    INDEX idx_exam_id (exam_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table: responses
-- Description: Stores student answers for each question
-- ============================================
CREATE TABLE responses (
    response_id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option CHAR(1),
    is_correct BOOLEAN DEFAULT FALSE,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    UNIQUE KEY unique_attempt_question (attempt_id, question_id),
    INDEX idx_attempt_id (attempt_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table: proctoring_logs
-- Description: Stores AI proctoring events during exam
-- ============================================
CREATE TABLE proctoring_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    event_type ENUM('MULTIPLE_FACES', 'NO_FACE', 'FACE_DETECTED', 'SUSPICIOUS_ACTIVITY') NOT NULL,
    event_description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    severity ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'LOW',
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE,
    INDEX idx_attempt_id (attempt_id),
    INDEX idx_event_type (event_type),
    INDEX idx_severity (severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Create Views for Analytics
-- ============================================

-- View: Student Results Summary
CREATE VIEW student_results_view AS
SELECT 
    u.user_id,
    u.full_name,
    u.email,
    e.exam_title,
    ea.attempt_id,
    ea.start_time,
    ea.end_time,
    ea.score,
    e.total_marks,
    ea.status,
    CASE 
        WHEN ea.score >= e.passing_marks THEN 'PASS'
        ELSE 'FAIL'
    END AS result
FROM exam_attempts ea
JOIN users u ON ea.user_id = u.user_id
JOIN exams e ON ea.exam_id = e.exam_id
WHERE ea.status = 'COMPLETED' OR ea.status = 'AUTO_SUBMITTED';

-- View: Exam Statistics
CREATE VIEW exam_statistics_view AS
SELECT 
    e.exam_id,
    e.exam_title,
    COUNT(DISTINCT ea.user_id) AS total_attempts,
    AVG(ea.score) AS average_score,
    MAX(ea.score) AS highest_score,
    MIN(ea.score) AS lowest_score,
    COUNT(CASE WHEN ea.score >= e.passing_marks THEN 1 END) AS passed_count,
    COUNT(CASE WHEN ea.score < e.passing_marks THEN 1 END) AS failed_count
FROM exams e
LEFT JOIN exam_attempts ea ON e.exam_id = ea.exam_id
WHERE ea.status IN ('COMPLETED', 'AUTO_SUBMITTED')
GROUP BY e.exam_id, e.exam_title;

-- ============================================
-- End of Schema
-- ============================================
