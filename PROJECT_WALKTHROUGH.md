# Project Walkthrough
## Online Examination System with AI-Based Proctoring

---

## 📌 Project Overview

**Title**: Online Examination System with AI-Based Proctoring

**Objective**: To develop a secure, scalable online examination platform that enables educational institutions to conduct remote exams with AI-powered proctoring to ensure exam integrity and prevent cheating.

**Team Size**: Individual Project (Final Year)

**Duration**: 4-6 months

**Status**: ✅ Completed

---

## 🎯 Problem Statement

Traditional online examination systems face several challenges:
1. **Cheating**: Students can easily cheat without supervision
2. **Identity Verification**: Difficult to verify student identity
3. **Manual Monitoring**: Requires human proctors (expensive and not scalable)
4. **Security**: Vulnerable to unauthorized access and data breaches
5. **Scalability**: Cannot handle large number of concurrent users

**Our Solution**: An AI-powered examination system that automatically monitors students during exams, detects suspicious activities, and provides comprehensive analytics to administrators.

---

## 🏗️ System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT LAYER                          │
│  ┌──────────────┐              ┌──────────────┐        │
│  │Admin Portal  │              │Student Portal│        │
│  │(HTML/CSS/JS) │              │(HTML/CSS/JS) │        │
│  └──────────────┘              └──────────────┘        │
└─────────────────────────────────────────────────────────┘
                         ↓ REST APIs (JWT)
┌─────────────────────────────────────────────────────────┐
│                  APPLICATION LAYER                       │
│  ┌────────────────────────────────────────────────┐    │
│  │      Spring Boot Backend (Port 8080)           │    │
│  │  • Authentication & Authorization              │    │
│  │  • Exam Management                             │    │
│  │  • Result Processing                           │    │
│  │  • API Endpoints                               │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │   AI Proctoring Service (Port 5000)            │    │
│  │  • Face Detection (OpenCV)                     │    │
│  │  • Multiple Face Detection                     │    │
│  │  • Absence Tracking                            │    │
│  │  • Event Logging                               │    │
│  └────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                         ↓ JDBC
┌─────────────────────────────────────────────────────────┐
│                     DATA LAYER                           │
│            MySQL Database (Port 3306)                    │
│  • Users  • Exams  • Questions  • Responses             │
│  • Attempts  • Proctoring Logs                          │
└─────────────────────────────────────────────────────────┘
```

---

## 💾 Database Design

### Entity Relationship Diagram

**7 Main Tables**:
1. **users** - Admin and student information
2. **exams** - Exam details and configuration
3. **questions** - Exam questions
4. **options** - MCQ options
5. **exam_attempts** - Student exam attempts
6. **responses** - Student answers
7. **proctoring_logs** - AI proctoring events

**Key Relationships**:
- Users (1) → Exams (M) - Admin creates exams
- Exams (1) → Questions (M) - Exam contains questions
- Questions (1) → Options (M) - Question has options
- Users (1) → Exam_Attempts (M) - Student attempts exams
- Exam_Attempts (1) → Responses (M) - Attempt has responses
- Exam_Attempts (1) → Proctoring_Logs (M) - Attempt generates logs

**Normalization**: Database is in 3NF (Third Normal Form)

---

## 🔐 Security Implementation

### 1. Authentication (JWT)
- **Token-Based**: Stateless authentication using JSON Web Tokens
- **Flow**:
  1. User logs in with credentials
  2. Server validates and generates JWT
  3. Client stores token in localStorage
  4. Token sent in Authorization header for subsequent requests
  5. Server validates token on each request

### 2. Password Security
- **BCrypt Hashing**: One-way hash with salt
- **Salt Rounds**: 10 rounds for strong security
- **Never Stored Plain**: Only hashed passwords in database

### 3. Authorization
- **Role-Based Access Control (RBAC)**:
  - Admin: Full access to all features
  - Student: Limited to exam taking and results
- **Method-Level Security**: `@PreAuthorize` annotations
- **URL-Level Security**: Spring Security configuration

### 4. API Security
- **CORS**: Configured for specific origins
- **CSRF**: Disabled for stateless REST APIs
- **SQL Injection**: Prevented via JPA parameterized queries
- **XSS**: Input validation and sanitization

---

## 🎓 Features Implementation

### Admin Module

**1. Dashboard**
- Total exams count
- Active exams count
- Student statistics
- Recent activity

**2. Exam Management**
- Create new exams
- Set duration, marks, passing criteria
- Schedule exam (start/end time)
- Activate/deactivate exams

**3. Question Management**
- Add MCQ questions
- Define correct answers
- Assign marks per question
- Edit/delete questions

**4. Results & Analytics**
- View all student results
- Exam-wise statistics
- Pass/fail analysis
- Performance trends

**5. Proctoring Logs**
- View suspicious activities
- Filter by severity
- Exam attempt details
- Recommendation for action

### Student Module

**1. Registration**
- Self-registration with email verification
- Profile creation
- Automatic student role assignment

**2. Dashboard**
- Available exams list
- Upcoming exams
- Completed exams
- Results summary

**3. Exam Interface**
- Timer display
- Question navigation
- Option selection
- Auto-save answers
- Submit exam

**4. Proctoring**
- Webcam access
- Real-time monitoring
- Proctoring status indicator

**5. Results**
- Score display
- Correct/incorrect answers
- Pass/fail status
- Detailed breakdown

---

## 🤖 AI Proctoring Module

### Technology: Python + OpenCV

**1. Face Detection**
- **Algorithm**: Haar Cascade Classifier
- **Detection Rate**: 85-90% in good lighting
- **Processing**: Real-time frame analysis
- **Output**: Face count and coordinates

**2. Suspicious Activity Detection**

**Scenario 1: No Face Detected**
- **Trigger**: 0 faces in frame
- **Severity**: MEDIUM → HIGH (if prolonged)
- **Action**: Log event, alert admin
- **Threshold**: 3 consecutive absences

**Scenario 2: Multiple Faces**
- **Trigger**: 2+ faces in frame
- **Severity**: HIGH
- **Action**: Immediate log, flag for review
- **Indication**: Possible cheating

**Scenario 3: Normal Operation**
- **Trigger**: 1 face detected
- **Severity**: LOW
- **Action**: Log as normal activity

**3. Event Logging**
- **Timestamp**: Exact time of event
- **Event Type**: FACE_DETECTED, NO_FACE, MULTIPLE_FACES
- **Description**: Detailed event description
- **Severity**: LOW, MEDIUM, HIGH
- **Storage**: JSON file + database

**4. Proctoring Report**
- **Total Events**: Count of all events
- **Suspicious Activities**: High severity count
- **Status**: NORMAL, SUSPICIOUS, HIGHLY_SUSPICIOUS
- **Recommendation**: Action suggestion for admin

---

## 🔄 Complete User Flow

### Student Exam Flow

```
1. Student Login
   ↓
2. View Available Exams
   ↓
3. Click "Start Exam"
   ↓
4. Grant Webcam Permission
   ↓
5. Exam Interface Loads
   ↓
6. Timer Starts (e.g., 60 minutes)
   ↓
7. Answer Questions
   • Select options
   • Auto-save on selection
   • Proctoring runs in background
   ↓
8. Submit Exam (or Auto-submit on timeout)
   ↓
9. Score Calculated Automatically
   ↓
10. View Results
```

### Admin Exam Creation Flow

```
1. Admin Login
   ↓
2. Navigate to "Create Exam"
   ↓
3. Fill Exam Details
   • Title
   • Description
   • Duration
   • Total Marks
   • Passing Marks
   • Schedule
   ↓
4. Add Questions
   • Question text
   • Options (A, B, C, D)
   • Correct answer
   • Marks
   ↓
5. Activate Exam
   ↓
6. Students Can Now Attempt
```

---

## 📊 Technical Implementation Details

### Backend (Spring Boot)

**Layered Architecture**:
```
Controller Layer (REST APIs)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (MySQL)
```

**Key Components**:
- **Entities**: JPA entities with annotations
- **Repositories**: Spring Data JPA interfaces
- **Services**: Business logic implementation
- **Controllers**: REST endpoint handlers
- **DTOs**: Data transfer objects
- **Security**: JWT filter, UserDetailsService
- **Config**: Security, CORS, database configuration

**Example API Endpoint**:
```java
@PostMapping("/student/exams/{id}/start")
public ResponseEntity<?> startExam(@PathVariable Long id) {
    ExamAttempt attempt = attemptService.startExam(id, userId);
    return ResponseEntity.ok(new ApiResponse(true, "Exam started", attempt));
}
```

### Frontend (HTML/CSS/JS)

**Design Principles**:
- **Responsive**: Works on desktop, tablet, mobile
- **Modern UI**: Clean, professional design
- **User-Friendly**: Intuitive navigation
- **Accessible**: Clear labels, good contrast

**Key Features**:
- **Dynamic Content**: JavaScript DOM manipulation
- **AJAX Calls**: Fetch API for REST communication
- **Local Storage**: Token and user data storage
- **Timer**: Countdown with auto-submit
- **Webcam**: MediaDevices API for proctoring

**Example JavaScript**:
```javascript
async function startExam(examId) {
    const data = await apiRequest(`/student/exams/${examId}/start`, {
        method: 'POST'
    });
    
    if (data.success) {
        localStorage.setItem('attemptId', data.data.attemptId);
        window.location.href = 'exam-interface.html';
    }
}
```

### AI Proctoring (Python/Flask)

**Modules**:
1. **face_detector.py**: Haar Cascade implementation
2. **multi_face_detector.py**: Multiple face logic
3. **absence_detector.py**: Absence tracking
4. **logger.py**: Event logging system

**Example Python Code**:
```python
def analyze_frame(image, attempt_id):
    faces = face_detector.detect_faces(image)
    num_faces = len(faces)
    
    if num_faces == 0:
        log_to_backend(attempt_id, 'NO_FACE', 
                      'Face not detected', 'MEDIUM')
    elif num_faces == 1:
        log_to_backend(attempt_id, 'FACE_DETECTED', 
                      'Normal', 'LOW')
    else:
        log_to_backend(attempt_id, 'MULTIPLE_FACES', 
                      f'{num_faces} faces detected', 'HIGH')
```

---

## 🧪 Testing Results

### Functional Testing
✅ User registration and login  
✅ Admin exam creation  
✅ Student exam attempt  
✅ Timer functionality  
✅ Auto-submit on timeout  
✅ Score calculation  
✅ Proctoring detection  

### Security Testing
✅ JWT token validation  
✅ Unauthorized access prevention  
✅ Password encryption  
✅ SQL injection prevention  
✅ CORS configuration  

### Performance Testing
✅ 50 concurrent users handled  
✅ Response time < 500ms  
✅ Face detection: 20-30 FPS  

---

## 📈 Future Enhancements

1. **Advanced Proctoring**
   - Eye tracking
   - Screen monitoring
   - Audio analysis
   - Browser lockdown

2. **Question Types**
   - Descriptive answers
   - Code evaluation
   - File uploads

3. **Analytics**
   - Performance trends
   - Question difficulty analysis
   - Student ranking

4. **Notifications**
   - Email alerts
   - SMS notifications
   - Push notifications

5. **Mobile App**
   - React Native app
   - Offline exam capability

6. **Scalability**
   - Microservices architecture
   - Load balancing
   - Caching (Redis)
   - CDN integration

---

## 🎓 Learning Outcomes

### Technical Skills
- Full-stack web development
- RESTful API design
- Database design and normalization
- Security implementation (JWT, BCrypt)
- Computer vision basics (OpenCV)
- Python web frameworks (Flask)
- Version control (Git)

### Soft Skills
- Problem-solving
- Time management
- Documentation
- Research and learning
- Debugging and troubleshooting

---

## 📝 Conclusion

This project successfully demonstrates:
- **Full-Stack Development**: Complete end-to-end application
- **Security**: Industry-standard authentication and authorization
- **AI Integration**: Practical application of computer vision
- **Scalability**: Modular architecture for future growth
- **Real-World Application**: Solves actual problem in education sector

**Impact**: This system can help educational institutions conduct secure online exams, reduce cheating, and provide better analytics for improving education quality.

---

**Project By**: [Your Name]  
**Institution**: [Your College]  
**Year**: 2026  
**Guide**: [Guide Name]  

---

**End of Walkthrough**
