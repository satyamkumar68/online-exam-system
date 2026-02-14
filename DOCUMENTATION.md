# Online Examination System with AI-Based Proctoring

## 📋 Table of Contents
- [Overview](#overview)
- [System Architecture](#system-architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Installation & Setup](#installation--setup)
- [Usage Guide](#usage-guide)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security Features](#security-features)
- [Future Enhancements](#future-enhancements)

---

## 🎯 Overview

The **Online Examination System** is a comprehensive web-based platform that enables educational institutions to conduct secure online examinations with AI-powered proctoring capabilities. The system provides separate interfaces for administrators and students, with features including exam scheduling, question bank management, real-time proctoring, result analytics, and automated certificate generation.

### Key Highlights
- 🤖 **AI-Powered Proctoring** using computer vision
- 📊 **Real-time Analytics Dashboard** with interactive charts
- 🗂️ **Question Bank Management** for reusable questions
- ⏰ **Exam Scheduling** with time-based access control
- 📜 **Automated PDF Certificates** for passed exams
- 🔐 **Secure Authentication** with JWT tokens
- 📱 **Responsive Design** for all devices

---

## 🏗️ System Architecture

### Architecture Diagram
```
┌─────────────────────────────────────────────────────────────┐
│                         Frontend Layer                       │
│  (HTML5, CSS3, JavaScript, Bootstrap 5, Chart.js, jsPDF)   │
│                    Port: 7001 (HTTP Server)                  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Backend Layer                           │
│         (Spring Boot 3, Spring Security, JWT)               │
│                    Port: 8081 (REST API)                     │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
┌────────────────┐ ┌──────────┐ ┌──────────────────┐
│   MySQL DB     │ │  Flask   │ │  File System     │
│   Port: 3306   │ │  AI Proc │ │  (Certificates)  │
│                │ │Port: 7003│ │                  │
└────────────────┘ └──────────┘ └──────────────────┘
```

### Component Overview

#### 1. **Frontend (Port 7001)**
- **Technology**: Vanilla HTML5, CSS3, JavaScript
- **UI Framework**: Bootstrap 5
- **Libraries**: Chart.js (analytics), jsPDF (certificates)
- **Purpose**: User interface for admin and student interactions

#### 2. **Backend API (Port 8081)**
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security + JWT
- **Database**: JPA/Hibernate with MySQL
- **Purpose**: Business logic, authentication, data management

#### 3. **AI Proctoring Service (Port 7003)**
- **Framework**: Flask (Python)
- **AI Library**: OpenCV with Haar Cascade
- **Purpose**: Real-time face detection and tab-switching monitoring

#### 4. **Database (Port 3306)**
- **DBMS**: MySQL 8.x
- **Purpose**: Persistent storage for users, exams, questions, results

---

## ✨ Features

### Feature 1: User Authentication & Authorization
**Description**: Secure login system with role-based access control.

**Technologies Used**:
- Spring Security
- JWT (JSON Web Tokens)
- BCrypt password hashing

**Functionality**:
- User registration with email validation
- Secure login with JWT token generation
- Role-based access (ADMIN, STUDENT)
- Session management
- Password encryption

**Files**:
- Backend: `AuthController.java`, `JwtUtils.java`, `UserDetailsServiceImpl.java`
- Frontend: `login.html`, `signup.html`, `auth.js`

---

### Feature 2: Admin Dashboard & Exam Management
**Description**: Comprehensive admin interface for managing exams, students, and viewing analytics.

**Technologies Used**:
- Spring Boot REST APIs
- Bootstrap 5 for UI
- Chart.js for data visualization

**Functionality**:
- Create, view, and delete exams
- Set exam duration and time limits
- View all registered students
- Access result analytics with charts
- Manage question bank

**Files**:
- Backend: `AdminController.java`, `Exam.java`, `ExamRepository.java`
- Frontend: `admin_dashboard.html`, `create_exam.html`, `manage_students.html`

---

### Feature 3: Student Dashboard & Exam Taking
**Description**: Student interface for viewing available exams and taking tests.

**Technologies Used**:
- Spring Boot REST APIs
- JavaScript for dynamic content
- LocalStorage for session management

**Functionality**:
- View available exams with status badges
- Take exams within scheduled time windows
- Real-time timer during exam
- Submit answers and view results
- Download certificates for passed exams

**Files**:
- Backend: `StudentController.java`, `ExamResult.java`
- Frontend: `student_dashboard.html`, `take_exam.html`, `view_results.html`

---

### Feature 4: AI-Based Proctoring
**Description**: Real-time monitoring of students during exams using computer vision.

**Technologies Used**:
- **Python Flask**: Web server for proctoring API
- **OpenCV**: Computer vision library
- **Haar Cascade Classifier**: Face detection algorithm
- **NumPy**: Array processing

**Functionality**:
- **Face Detection**: Monitors student presence via webcam
- **Tab Switching Detection**: Tracks when students leave exam tab
- **Warning System**: Alerts students of violations
- **Real-time Monitoring**: Continuous frame analysis (every 3 seconds)

**Algorithm**:
```python
1. Capture webcam frame
2. Convert to grayscale
3. Apply Haar Cascade face detection
4. Check brightness levels
5. Return detection status
```

**Files**:
- Backend: `app.py` (Flask service)
- Frontend: `take_exam.html` (webcam integration)
- Model: `haarcascade_frontalface_default.xml`

**Configuration**:
- Detection interval: 3000ms
- Minimum face size: 30x30 pixels
- Scale factor: 1.1
- Minimum neighbors: 5

---

### Feature 5: Question Bank Management
**Description**: Centralized repository for reusable exam questions with categorization.

**Technologies Used**:
- Spring Boot JPA
- MySQL database
- Bootstrap tabs

**Functionality**:
- Create questions with categories (e.g., "Java Basics", "SQL", "Math")
- Browse questions by category
- Select multiple questions for exams
- Assign bank questions to exams
- Delete unused questions

**Database Changes**:
```sql
ALTER TABLE questions 
ADD COLUMN category VARCHAR(100),
MODIFY COLUMN exam_id BIGINT NULL;
```

**Files**:
- Backend: `AdminController.java` (question bank endpoints)
- Frontend: `question_bank.html`, `add_questions.html`

**API Endpoints**:
- `GET /api/admin/question-bank` - Get all bank questions
- `GET /api/admin/question-bank/categories` - Get categories
- `POST /api/admin/question-bank` - Add question to bank
- `POST /api/admin/exams/{examId}/add-bank-question/{questionId}` - Assign to exam

---

### Feature 6: Exam Scheduling (Time Limits)
**Description**: Time-based access control for exams with automatic status updates.

**Technologies Used**:
- Java LocalDateTime
- JavaScript Date API
- Bootstrap badges

**Functionality**:
- Set start and end times for exams
- Automatic status calculation (Upcoming/Active/Expired)
- Disable exam access outside time window
- Visual status indicators

**Status Logic**:
```javascript
if (currentTime < startTime) → "Upcoming"
if (startTime ≤ currentTime ≤ endTime) → "Active"
if (currentTime > endTime) → "Expired"
```

**Files**:
- Backend: `Exam.java`, `StudentController.java`
- Frontend: `create_exam.html`, `student_dashboard.html`

---

### Feature 7: Results Analytics & Dashboard
**Description**: Comprehensive analytics with interactive charts and statistics.

**Technologies Used**:
- **Chart.js 3.x**: JavaScript charting library
- **Spring Boot**: Analytics API
- **Bootstrap Cards**: UI components

**Functionality**:
- **Statistics Cards**:
  - Total Students
  - Total Exams
  - Average Score (%)
  
- **Performance Chart**:
  - Doughnut chart showing pass/fail ratio
  - Color-coded segments (green for pass, red for fail)
  
- **Real-time Updates**: Data refreshes on page load

**Metrics Calculated**:
```java
Average Score = (Sum of all scores / Total results) * 100
Pass Count = Results with score ≥ 50%
Fail Count = Results with score < 50%
```

**Files**:
- Backend: `AdminController.java` (analytics endpoint), `AnalyticsResponse.java`
- Frontend: `admin_dashboard.html`

---

### Feature 8: PDF Certificate Generation
**Description**: Automated certificate generation for students who pass exams.

**Technologies Used**:
- **jsPDF**: Client-side PDF generation library
- **JavaScript**: Certificate design logic

**Functionality**:
- Generate professional certificates
- Include student name, exam title, score, date
- Customizable design with colors and fonts
- Download as PDF file
- Only available for passed exams (score ≥ 50%)

**Certificate Design**:
- Header with title
- Student details
- Exam information
- Score and percentage
- Date of completion
- Professional formatting

**Files**:
- Frontend: `student_dashboard.html` (certificate generation function)

---

### Feature 9: Registration System
**Description**: Self-service student registration with validation.

**Technologies Used**:
- Spring Boot validation
- BCrypt password hashing
- Bootstrap forms

**Functionality**:
- User registration form
- Email and password validation
- Automatic role assignment (STUDENT)
- Duplicate email prevention
- Secure password storage

**Files**:
- Backend: `AuthController.java`, `SignupRequest.java`
- Frontend: `signup.html`

---

## 🛠️ Technology Stack

### Backend Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17+ | Programming language |
| **Spring Boot** | 3.x | Application framework |
| **Spring Security** | 6.x | Authentication & authorization |
| **Spring Data JPA** | 3.x | Database ORM |
| **Hibernate** | 6.x | JPA implementation |
| **MySQL Connector** | 8.x | Database driver |
| **JWT (jjwt)** | 0.11.x | Token-based authentication |
| **Lombok** | 1.18.x | Boilerplate code reduction |
| **Maven** | 3.x | Build tool |

### Frontend Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **HTML5** | - | Structure |
| **CSS3** | - | Styling |
| **JavaScript (ES6+)** | - | Interactivity |
| **Bootstrap** | 5.3.0 | UI framework |
| **Chart.js** | 3.x | Data visualization |
| **jsPDF** | 2.x | PDF generation |

### AI Proctoring Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Python** | 3.8+ | Programming language |
| **Flask** | 2.x | Web framework |
| **OpenCV** | 4.x | Computer vision |
| **NumPy** | 1.x | Array processing |
| **python-dotenv** | 1.x | Environment variables |

### Database

| Technology | Version | Purpose |
|------------|---------|---------|
| **MySQL** | 8.x | Relational database |

### Development Tools

| Tool | Purpose |
|------|---------|
| **Git** | Version control |
| **VS Code** | Code editor |
| **Postman** | API testing |
| **MySQL Workbench** | Database management |

---

## 📁 Project Structure

```
Online-Examination-System/
│
├── backend/                          # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/exam/system/
│   │   │   │   ├── controller/       # REST Controllers
│   │   │   │   │   ├── AdminController.java
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   └── StudentController.java
│   │   │   │   ├── entity/           # JPA Entities
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Exam.java
│   │   │   │   │   ├── Question.java
│   │   │   │   │   └── ExamResult.java
│   │   │   │   ├── repository/       # JPA Repositories
│   │   │   │   ├── security/         # Security Config
│   │   │   │   │   ├── jwt/
│   │   │   │   │   └── services/
│   │   │   │   └── payload/          # DTOs
│   │   │   │       ├── request/
│   │   │   │       └── response/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml                       # Maven dependencies
│
├── frontend/                         # Frontend Files
│   ├── login.html                    # Login page
│   ├── signup.html                   # Registration page
│   ├── admin_dashboard.html          # Admin dashboard
│   ├── create_exam.html              # Create exam form
│   ├── add_questions.html            # Add questions (with bank)
│   ├── question_bank.html            # Question bank management
│   ├── manage_students.html          # Student management
│   ├── student_dashboard.html        # Student dashboard
│   ├── take_exam.html                # Exam interface
│   ├── view_results.html             # Results page
│   └── js/
│       ├── api.js                    # API helper functions
│       └── auth.js                   # Authentication utilities
│
├── proctoring-service/               # AI Proctoring Service
│   ├── app.py                        # Flask application
│   ├── requirements.txt              # Python dependencies
│   ├── .env                          # Environment variables
│   └── haarcascade_frontalface_default.xml
│
├── database/                         # Database Scripts
│   └── sample_data.sql               # Sample data
│
├── start_all.bat                     # Windows startup script
├── .gitignore                        # Git ignore rules
└── README.md                         # This file
```

---

## 🚀 Installation & Setup

### Prerequisites
- **Java JDK** 17 or higher
- **Maven** 3.6+
- **MySQL** 8.0+
- **Python** 3.8+
- **Node.js** (for http-server)
- **Git**

### Step 1: Clone Repository
```bash
git clone <repository-url>
cd Online-Examination-System
```

### Step 2: Database Setup
```sql
-- Create database
CREATE DATABASE online_exam_system;

-- Use database
USE online_exam_system;

-- Tables will be auto-created by Hibernate
```

### Step 3: Configure Backend
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/online_exam_system
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
jwt.secret=YOUR_SECRET_KEY
```

### Step 4: Install Backend Dependencies
```bash
cd backend
mvn clean install
```

### Step 5: Setup Proctoring Service
```bash
cd proctoring-service
pip install -r requirements.txt
```

Create `.env` file:
```
BACKEND_URL=http://127.0.0.1:8081
```

### Step 6: Install Frontend Server
```bash
npm install -g http-server
```

### Step 7: Start All Services
```bash
# Run from project root
.\start_all.bat
```

This will start:
- Backend API on `http://127.0.0.1:8081`
- Frontend on `http://127.0.0.1:7001`
- Proctoring Service on `http://127.0.0.1:7003`

---

## 📖 Usage Guide

### For Administrators

#### 1. **Login**
- Navigate to `http://127.0.0.1:7001/login.html`
- Use admin credentials
- Default: `admin@example.com` / `admin123`

#### 2. **Create Exam**
1. Click "Create New Exam" button
2. Fill in exam details:
   - Title
   - Description
   - Duration (minutes)
   - Start Time
   - End Time
3. Click "Create Exam"

#### 3. **Add Questions**
**Method 1: From Question Bank**
1. Go to exam → "Add Questions"
2. Select "Select from Question Bank" tab
3. Filter by category (optional)
4. Check questions to add
5. Click "Add Selected to Exam"

**Method 2: Create New**
1. Go to exam → "Add Questions"
2. Select "Create New Question" tab
3. Fill in question details
4. Click "Add Question"

#### 4. **Manage Question Bank**
1. Click "Question Bank" button
2. Click "Add Question"
3. Enter question with category
4. Questions are now reusable across exams

#### 5. **View Analytics**
- Dashboard shows:
  - Total students
  - Total exams
  - Average score
  - Pass/Fail chart

### For Students

#### 1. **Register**
- Go to `http://127.0.0.1:7001/signup.html`
- Fill registration form
- Login with credentials

#### 2. **Take Exam**
1. View available exams on dashboard
2. Check exam status (Upcoming/Active/Expired)
3. Click "Take Exam" (only for active exams)
4. Allow webcam access for proctoring
5. Answer questions
6. Click "Submit Exam"

#### 3. **View Results**
- Results appear on dashboard after submission
- Shows score, percentage, pass/fail status

#### 4. **Download Certificate**
- Available for passed exams (≥50%)
- Click "Download Certificate" button
- PDF downloads automatically

---

## 🔌 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "student1",
  "email": "student1@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "student1",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "student1",
  "email": "student1@example.com",
  "roles": ["STUDENT"]
}
```

### Admin Endpoints

#### Create Exam
```http
POST /api/admin/exams
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Java Fundamentals",
  "description": "Basic Java concepts",
  "maxTimeMinutes": 60,
  "startTime": "2024-03-01T10:00:00",
  "endTime": "2024-03-01T18:00:00"
}
```

#### Get All Exams
```http
GET /api/admin/exams
Authorization: Bearer <token>
```

#### Add Question to Bank
```http
POST /api/admin/question-bank
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "What is polymorphism?",
  "option1": "Inheritance",
  "option2": "Many forms",
  "option3": "Encapsulation",
  "option4": "Abstraction",
  "answer": "option2",
  "category": "Java OOP"
}
```

#### Get Question Bank
```http
GET /api/admin/question-bank
Authorization: Bearer <token>
```

#### Assign Bank Question to Exam
```http
POST /api/admin/exams/{examId}/add-bank-question/{questionId}
Authorization: Bearer <token>
```

#### Get Analytics
```http
GET /api/admin/analytics
Authorization: Bearer <token>

Response:
{
  "totalStudents": 50,
  "totalExams": 10,
  "averageScore": 75.5,
  "passCount": 35,
  "failCount": 15
}
```

### Student Endpoints

#### Get Available Exams
```http
GET /api/student/exams
Authorization: Bearer <token>
```

#### Get Exam Questions
```http
GET /api/student/exams/{examId}/questions
Authorization: Bearer <token>
```

#### Submit Exam
```http
POST /api/student/exams/{examId}/submit
Authorization: Bearer <token>
Content-Type: application/json

{
  "answers": {
    "1": "option2",
    "2": "option1",
    "3": "option4"
  }
}
```

#### Get Results
```http
GET /api/student/results
Authorization: Bearer <token>
```

### Proctoring Endpoints

#### Verify Face
```http
POST /proctoring/verify
Content-Type: application/json

{
  "image": "base64_encoded_image_data",
  "userId": 123,
  "examId": 456
}

Response:
{
  "faceDetected": true,
  "message": "Face detected successfully"
}
```

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role ENUM('ADMIN', 'STUDENT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Exams Table
```sql
CREATE TABLE exams (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    max_time_minutes INT NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Questions Table
```sql
CREATE TABLE questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    option1 VARCHAR(255) NOT NULL,
    option2 VARCHAR(255) NOT NULL,
    option3 VARCHAR(255) NOT NULL,
    option4 VARCHAR(255) NOT NULL,
    answer VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    exam_id BIGINT NULL,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);
```

### Exam Results Table
```sql
CREATE TABLE exam_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    exam_id BIGINT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);
```

---

## 🔐 Security Features

### 1. **Authentication**
- JWT-based stateless authentication
- Token expiration (24 hours)
- Secure password hashing with BCrypt (strength: 10)

### 2. **Authorization**
- Role-based access control (RBAC)
- Admin-only endpoints protected
- Student-only endpoints protected

### 3. **CORS Configuration**
- Configured allowed origins
- Prevents unauthorized cross-origin requests

### 4. **Input Validation**
- Server-side validation using Bean Validation
- SQL injection prevention via JPA
- XSS protection

### 5. **Proctoring Security**
- Real-time monitoring
- Tab switching detection
- Face detection verification

---

## 🎨 UI/UX Features

### Design Principles
- **Modern & Clean**: Professional appearance
- **Responsive**: Works on all devices
- **Intuitive**: Easy navigation
- **Accessible**: Clear labels and feedback

### Color Scheme
- Primary: `#4361ee` (Blue)
- Success: `#28a745` (Green)
- Danger: `#dc3545` (Red)
- Warning: `#ffc107` (Yellow)

### Components
- Bootstrap 5 cards
- Gradient navbar
- Interactive charts
- Modal dialogs
- Toast notifications
- Badge indicators

---

## 🚀 Future Enhancements

1. **Email Notifications**
   - Exam reminders
   - Result notifications
   - Certificate delivery

2. **Advanced Proctoring**
   - Eye tracking
   - Multiple face detection
   - Audio monitoring
   - Screen recording

3. **Question Types**
   - Multiple correct answers
   - True/False
   - Fill in the blanks
   - Essay questions

4. **Reporting**
   - Detailed analytics
   - Export to Excel/PDF
   - Student performance trends

5. **Mobile App**
   - Native Android/iOS apps
   - Push notifications

6. **Integration**
   - LMS integration (Moodle, Canvas)
   - Google Classroom
   - Microsoft Teams

---

## 📞 Support & Contact

For issues, questions, or contributions:
- **Email**: support@examsystem.com
- **GitHub**: [Repository Link]
- **Documentation**: [Wiki Link]

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Contributors

- **Developer**: [Your Name]
- **Project Type**: Academic/Commercial
- **Year**: 2024

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- OpenCV Community
- Bootstrap Team
- Chart.js Developers
- Stack Overflow Community

---

**Last Updated**: February 14, 2024
**Version**: 1.0.0
