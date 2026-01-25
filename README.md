# Online Examination System 🎓

A comprehensive, secure, and feature-rich online examination platform with AI-based proctoring.

![Project Status](https://img.shields.io/badge/Status-Completed-success)
![License](https://img.shields.io/badge/License-MIT-blue)

## 🚀 Key Features

### 👨‍🎓 For Students
- **Dashboard**: View available, upcoming, and completed exams.
- **Secure Exam Interface**: Full-screen mode with anti-cheating mechanisms.
- **AI Proctoring**: Real-time webcam monitoring to detect multiple faces, no face, or looking away.
- **Instant Results**: View score immediately after submission.
- **Certificates**: Download professional PDF certificates for passed exams.

### 👨‍🏫 For Admins
- **Exam Management**: Create, Edit, Activate, Deactivate, and Delete exams.
- **Bulk Import**: Upload CSV files to create questions instantly.
- **Result Analytics**: View detailed attempt history for all students.
- **Security Logs**: Monitor proctoring alerts (cheating attempts).

## 🛠️ Tech Stack

- **Backend**: Java (Spring Boot), Hibernate, JPA, MySQL, JJWT (Security).
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla), Fetch API.
- **AI Service**: Python (Flask), OpenCV, MediaPipe (Face Mesh).
- **Build Tools**: Maven.

## ⚙️ Setup Instructions

### Prerequisites
- Java JDK 11+
- Maven 3.6+
- Python 3.8+
- MySQL Server

### 1. Database Setup
Create a MySQL database named `online_exam_system`.
```sql
CREATE DATABASE online_exam_system;
```
Configure your credentials in `backend/src/main/resources/application.properties` (or use environment variables).

### 2. Backend (Spring Boot)
```bash
cd backend
mvn clean package
java -jar target/online-examination-system-1.0.0.jar
```
*Server runs on Port 8081*

### 3. AI Proctoring Service (Python)
```bash
cd proctoring-service
pip install -r requirements.txt
python app.py
```
*Service runs on Port 5000*

### 4. Frontend
Since it's static HTML/JS, you can run it with any static server.
```bash
cd frontend
python -m http.server 5500
```
*Access at http://localhost:5500*

## 🛡️ Security Highlights
- **IDOR Protection**: Strict checks ensure students can only submit their own exams.
- **XSS Prevention**: All user inputs (exam titles, descriptions) are sanitized.
- **Constraint Safety**: Database cascading ensures safe deletion of exams.
- **CSRF/CORS**: Configured for modern web security standards.

## 🤝 Contribution
Feel free to fork this repository and submit Pull Requests!

## 📄 License
This project is licensed under the MIT License.
