-- ============================================
-- Online Examination System - Sample Data
-- ============================================

USE online_exam_system;

-- ============================================
-- Insert Sample Users
-- ============================================
-- Password for all users: password123 (BCrypt encoded)
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (username, email, password, full_name, role, is_active) VALUES
('admin', 'admin@examportal.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'ADMIN', TRUE),
('john.doe', 'john.doe@student.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John Doe', 'STUDENT', TRUE),
('jane.smith', 'jane.smith@student.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jane Smith', 'STUDENT', TRUE),
('robert.brown', 'robert.brown@student.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Robert Brown', 'STUDENT', TRUE),
('emily.davis', 'emily.davis@student.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Emily Davis', 'STUDENT', TRUE);

-- ============================================
-- Insert Sample Exams
-- ============================================
INSERT INTO exams (exam_title, exam_description, duration_minutes, total_marks, passing_marks, created_by, is_active, start_time, end_time) VALUES
('Java Programming Fundamentals', 'Test your knowledge of core Java concepts including OOP, collections, and exception handling', 60, 50, 25, 1, TRUE, '2026-01-20 10:00:00', '2026-01-20 23:59:59'),
('Database Management Systems', 'Comprehensive exam covering SQL, normalization, transactions, and database design', 45, 40, 20, 1, TRUE, '2026-01-22 14:00:00', '2026-01-22 23:59:59'),
('Web Development Basics', 'HTML, CSS, JavaScript fundamentals and responsive design principles', 30, 30, 15, 1, TRUE, '2026-01-25 09:00:00', '2026-01-25 23:59:59'),
('Data Structures and Algorithms', 'Arrays, linked lists, trees, sorting, and searching algorithms', 90, 60, 30, 1, TRUE, '2026-01-28 11:00:00', '2026-01-28 23:59:59');

-- ============================================
-- Insert Sample Questions for Java Exam (exam_id = 1)
-- ============================================
INSERT INTO questions (exam_id, question_text, question_type, marks, correct_option) VALUES
(1, 'Which of the following is NOT a principle of Object-Oriented Programming?', 'MCQ', 2, 'D'),
(1, 'What is the default value of a boolean variable in Java?', 'MCQ', 2, 'B'),
(1, 'Which keyword is used to prevent method overriding in Java?', 'MCQ', 2, 'C'),
(1, 'What is the output of: System.out.println(10 + 20 + "Hello");', 'MCQ', 2, 'A'),
(1, 'Which collection class allows duplicate elements and maintains insertion order?', 'MCQ', 2, 'B'),
(1, 'What is the parent class of all exception classes in Java?', 'MCQ', 2, 'A'),
(1, 'Which of these is a marker interface in Java?', 'MCQ', 2, 'D'),
(1, 'What is the size of int data type in Java?', 'MCQ', 2, 'C'),
(1, 'Which method is used to start a thread in Java?', 'MCQ', 2, 'B'),
(1, 'Can we override static methods in Java?', 'TRUE_FALSE', 2, 'B');

-- ============================================
-- Insert Options for Java Questions
-- ============================================
-- Question 1 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(1, 'Encapsulation', 'A'),
(1, 'Inheritance', 'B'),
(1, 'Polymorphism', 'C'),
(1, 'Compilation', 'D');

-- Question 2 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(2, 'null', 'A'),
(2, 'false', 'B'),
(2, 'true', 'C'),
(2, '0', 'D');

-- Question 3 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(3, 'static', 'A'),
(3, 'private', 'B'),
(3, 'final', 'C'),
(3, 'abstract', 'D');

-- Question 4 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(4, '30Hello', 'A'),
(4, '1020Hello', 'B'),
(4, 'Hello30', 'C'),
(4, 'Compilation Error', 'D');

-- Question 5 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(5, 'HashSet', 'A'),
(5, 'ArrayList', 'B'),
(5, 'TreeSet', 'C'),
(5, 'HashMap', 'D');

-- Question 6 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(6, 'Throwable', 'A'),
(6, 'Exception', 'B'),
(6, 'Error', 'C'),
(6, 'RuntimeException', 'D');

-- Question 7 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(7, 'Runnable', 'A'),
(7, 'Comparable', 'B'),
(7, 'Cloneable', 'C'),
(7, 'Serializable', 'D');

-- Question 8 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(8, '2 bytes', 'A'),
(8, '8 bytes', 'B'),
(8, '4 bytes', 'C'),
(8, '1 byte', 'D');

-- Question 9 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(9, 'run()', 'A'),
(9, 'start()', 'B'),
(9, 'init()', 'C'),
(9, 'execute()', 'D');

-- Question 10 options (True/False)
INSERT INTO options (question_id, option_text, option_label) VALUES
(10, 'True', 'A'),
(10, 'False', 'B');

-- ============================================
-- Insert Sample Questions for Database Exam (exam_id = 2)
-- ============================================
INSERT INTO questions (exam_id, question_text, question_type, marks, correct_option) VALUES
(2, 'Which normal form eliminates transitive dependencies?', 'MCQ', 2, 'C'),
(2, 'What does ACID stand for in database transactions?', 'MCQ', 2, 'A'),
(2, 'Which SQL command is used to remove a table from database?', 'MCQ', 2, 'D'),
(2, 'What is the purpose of an index in a database?', 'MCQ', 2, 'B'),
(2, 'Which join returns all rows from both tables?', 'MCQ', 2, 'C'),
(2, 'Primary key can contain NULL values?', 'TRUE_FALSE', 2, 'B'),
(2, 'Which clause is used to filter groups in SQL?', 'MCQ', 2, 'A'),
(2, 'What is a foreign key?', 'MCQ', 2, 'D');

-- ============================================
-- Insert Options for Database Questions
-- ============================================
-- Question 11 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(11, '1NF', 'A'),
(11, '2NF', 'B'),
(11, '3NF', 'C'),
(11, 'BCNF', 'D');

-- Question 12 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(12, 'Atomicity, Consistency, Isolation, Durability', 'A'),
(12, 'Accuracy, Consistency, Integrity, Durability', 'B'),
(12, 'Atomicity, Concurrency, Isolation, Dependency', 'C'),
(12, 'Accuracy, Concurrency, Integrity, Dependency', 'D');

-- Question 13 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(13, 'DELETE', 'A'),
(13, 'REMOVE', 'B'),
(13, 'TRUNCATE', 'C'),
(13, 'DROP', 'D');

-- Question 14 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(14, 'To enforce data integrity', 'A'),
(14, 'To speed up data retrieval', 'B'),
(14, 'To create relationships', 'C'),
(14, 'To store metadata', 'D');

-- Question 15 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(15, 'INNER JOIN', 'A'),
(15, 'LEFT JOIN', 'B'),
(15, 'FULL OUTER JOIN', 'C'),
(15, 'RIGHT JOIN', 'D');

-- Question 16 options (True/False)
INSERT INTO options (question_id, option_text, option_label) VALUES
(16, 'True', 'A'),
(16, 'False', 'B');

-- Question 17 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(17, 'HAVING', 'A'),
(17, 'WHERE', 'B'),
(17, 'GROUP BY', 'C'),
(17, 'ORDER BY', 'D');

-- Question 18 options
INSERT INTO options (question_id, option_text, option_label) VALUES
(18, 'A key that uniquely identifies records', 'A'),
(18, 'A key from another country', 'B'),
(18, 'A key used for encryption', 'C'),
(18, 'A key that references primary key of another table', 'D');

-- ============================================
-- Insert Sample Exam Attempts
-- ============================================
INSERT INTO exam_attempts (exam_id, user_id, start_time, end_time, score, status, is_proctored) VALUES
(1, 2, '2026-01-20 10:05:00', '2026-01-20 10:58:00', 42.00, 'COMPLETED', TRUE),
(1, 3, '2026-01-20 10:10:00', '2026-01-20 11:05:00', 38.00, 'COMPLETED', TRUE),
(2, 2, '2026-01-22 14:05:00', '2026-01-22 14:45:00', 32.00, 'COMPLETED', TRUE);

-- ============================================
-- Insert Sample Responses
-- ============================================
-- John's responses for Java exam (attempt_id = 1)
INSERT INTO responses (attempt_id, question_id, selected_option, is_correct) VALUES
(1, 1, 'D', TRUE),
(1, 2, 'B', TRUE),
(1, 3, 'C', TRUE),
(1, 4, 'A', TRUE),
(1, 5, 'B', TRUE),
(1, 6, 'A', TRUE),
(1, 7, 'D', TRUE),
(1, 8, 'C', TRUE),
(1, 9, 'B', TRUE),
(1, 10, 'B', TRUE);

-- Jane's responses for Java exam (attempt_id = 2)
INSERT INTO responses (attempt_id, question_id, selected_option, is_correct) VALUES
(2, 1, 'D', TRUE),
(2, 2, 'B', TRUE),
(2, 3, 'A', FALSE),
(2, 4, 'A', TRUE),
(2, 5, 'B', TRUE),
(2, 6, 'B', FALSE),
(2, 7, 'D', TRUE),
(2, 8, 'C', TRUE),
(2, 9, 'B', TRUE),
(2, 10, 'A', FALSE);

-- ============================================
-- Insert Sample Proctoring Logs
-- ============================================
INSERT INTO proctoring_logs (attempt_id, event_type, event_description, severity) VALUES
(1, 'FACE_DETECTED', 'Student face detected successfully', 'LOW'),
(1, 'NO_FACE', 'Face not detected for 3 seconds', 'MEDIUM'),
(1, 'FACE_DETECTED', 'Face detected again', 'LOW'),
(2, 'FACE_DETECTED', 'Student face detected successfully', 'LOW'),
(2, 'MULTIPLE_FACES', 'Multiple faces detected in frame', 'HIGH'),
(2, 'FACE_DETECTED', 'Single face detected', 'LOW'),
(3, 'FACE_DETECTED', 'Student face detected successfully', 'LOW');

-- ============================================
-- End of Sample Data
-- ============================================
