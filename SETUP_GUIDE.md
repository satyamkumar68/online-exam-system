# Online Examination System with AI-Based Proctoring
## Complete Setup and Execution Guide

---

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Database Setup](#database-setup)
3. [Backend Setup](#backend-setup)
4. [Frontend Setup](#frontend-setup)
5. [AI Proctoring Setup](#ai-proctoring-setup)
6. [Running the Complete System](#running-the-complete-system)
7. [Testing the Application](#testing-the-application)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software
- **Java JDK 11 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** - [Download](https://dev.mysql.com/downloads/mysql/)
- **Python 3.8+** - [Download](https://www.python.org/downloads/)
- **Git** - [Download](https://git-scm.com/downloads)
- **Web Browser** (Chrome/Firefox recommended)
- **Text Editor/IDE** (VS Code, IntelliJ IDEA, or Eclipse)

### Verify Installations
```bash
# Check Java
java -version

# Check Maven
mvn -version

# Check MySQL
mysql --version

# Check Python
python --version

# Check pip
pip --version
```

---

## Database Setup

### Step 1: Start MySQL Server
```bash
# Windows
net start MySQL80

# Linux/Mac
sudo systemctl start mysql
# or
sudo service mysql start
```

### Step 2: Login to MySQL
```bash
mysql -u root -p
# Enter your MySQL root password
```

### Step 3: Create Database and Tables
```sql
-- Run the schema script
source /path/to/database/schema.sql

-- Verify database creation
SHOW DATABASES;
USE online_exam_system;
SHOW TABLES;
```

### Step 4: Insert Sample Data
```sql
-- Run the sample data script
source /path/to/database/sample_data.sql

-- Verify data insertion
SELECT * FROM users;
SELECT * FROM exams;
```

### Step 5: Configure Database Credentials
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/online_exam_system
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

---

## Backend Setup

### Step 1: Navigate to Backend Directory
```bash
cd backend
```

### Step 2: Install Dependencies
```bash
mvn clean install
```

This will:
- Download all required dependencies
- Compile the Java code
- Run tests (if any)
- Create a JAR file

### Step 3: Run the Backend Server
```bash
mvn spring-boot:run
```

**Expected Output:**
```
==============================================
Online Examination System Started Successfully
Server running on: http://localhost:8080/api
==============================================
```

### Step 4: Verify Backend is Running
Open browser and visit: `http://localhost:8080/api/health` (if health endpoint exists)

Or test with curl:
```bash
curl http://localhost:8080/api/auth/login
```

---

## Frontend Setup

### Step 1: Navigate to Frontend Directory
```bash
cd frontend
```

### Step 2: Serve Frontend Files

**Option 1: Using Python HTTP Server**
```bash
# Python 3
python -m http.server 5500

# Python 2
python -m SimpleHTTPServer 5500
```

**Option 2: Using Node.js http-server**
```bash
# Install http-server globally
npm install -g http-server

# Run server
http-server -p 5500
```

**Option 3: Using VS Code Live Server Extension**
1. Install "Live Server" extension in VS Code
2. Right-click on `login.html`
3. Select "Open with Live Server"

### Step 3: Access the Application
Open browser and visit: `http://localhost:5500/login.html`

---

## AI Proctoring Setup

### Step 1: Navigate to Proctoring Service Directory
```bash
cd proctoring-service
```

### Step 2: Create Virtual Environment (Recommended)
```bash
# Create virtual environment
python -m venv venv

# Activate virtual environment
# Windows
venv\Scripts\activate

# Linux/Mac
source venv/bin/activate
```

### Step 3: Install Python Dependencies
```bash
pip install -r requirements.txt
```

This will install:
- Flask (web framework)
- OpenCV (computer vision)
- NumPy (numerical computing)
- Flask-CORS (cross-origin requests)
- Pillow (image processing)

### Step 4: Run the Proctoring Service
```bash
python app.py
```

**Expected Output:**
```
==================================================
AI Proctoring Service Starting...
Server running on: http://localhost:5000
==================================================
```

### Step 5: Test Proctoring Service
```bash
curl http://localhost:5000/health
```

---

## Running the Complete System

### Start All Services in Order

**Terminal 1: Database**
```bash
# Ensure MySQL is running
mysql -u root -p
```

**Terminal 2: Backend**
```bash
cd backend
mvn spring-boot:run
```

**Terminal 3: Proctoring Service**
```bash
cd proctoring-service
python app.py
```

**Terminal 4: Frontend**
```bash
cd frontend
python -m http.server 5500
```

### Service URLs
- **Frontend**: http://localhost:5500
- **Backend API**: http://localhost:8080/api
- **Proctoring Service**: http://localhost:5000
- **MySQL**: localhost:3306

---

## Testing the Application

### 1. Login as Admin
1. Open `http://localhost:5500/login.html`
2. Enter credentials:
   - Username: `newuser456`
   - Password: `password123`
3. Click "Login"
4. You should be redirected to Admin Dashboard

### 2. Admin Operations
- **Create Exam**: Navigate to "Manage Exams" → "Create New Exam"
- **Add Questions**: Select an exam → "Add Questions"
- **View Results**: Navigate to "Results" to see student performance
- **View Proctoring Logs**: Check suspicious activities

### 3. Login as Student
1. Logout from admin
2. Login with:
   - Username: `testuser999`
   - Password: `password123`
3. You should see Student Dashboard

### 4. Take an Exam
1. Click "Start Exam" on an available exam
2. Allow webcam access when prompted
3. Answer questions
4. Submit exam
5. View results

### 5. Register New Student
1. Click "Register" on login page
2. Fill in details:
   - Full Name: Your Name
   - Username: yourusername
   - Email: your@email.com
   - Password: yourpassword
3. Click "Register"
4. Login with new credentials

---

## Troubleshooting

### Database Connection Issues
**Problem**: `Communications link failure`

**Solution**:
```bash
# Check if MySQL is running
mysql -u root -p

# Verify database exists
SHOW DATABASES;

# Check application.properties has correct credentials
```

### Backend Port Already in Use
**Problem**: `Port 8080 already in use`

**Solution**:
```bash
# Windows - Find and kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

### Frontend CORS Errors
**Problem**: `CORS policy: No 'Access-Control-Allow-Origin'`

**Solution**:
- Ensure backend is running
- Check CORS configuration in `CorsConfig.java`
- Verify frontend URL is in allowed origins

### Proctoring Service Issues
**Problem**: `ModuleNotFoundError: No module named 'cv2'`

**Solution**:
```bash
# Reinstall OpenCV
pip uninstall opencv-python
pip install opencv-python==4.8.0.74
```

**Problem**: Webcam not working

**Solution**:
- Check browser permissions
- Ensure no other app is using webcam
- Try different browser (Chrome recommended)

### Maven Build Failures
**Problem**: `Failed to execute goal`

**Solution**:
```bash
# Clean and rebuild
mvn clean
mvn install -U

# Skip tests if needed
mvn clean install -DskipTests
```

---

## Login Credentials

### ✅ Working Admin Account
- **Username**: `newuser456`
- **Password**: `password123`
- **Login URL**: http://localhost:5500/login.html

### ✅ Working Student Account
- **Username**: `testuser999`
- **Password**: `password123`
- **Login URL**: http://localhost:5500/login.html

### ⚠️ Important Note
> **Note**: The original sample data users (`admin`, `john.doe`, `jane.smith`, etc.) have BCrypt password hash mismatches and **will not work** for login. Use the accounts listed above or register new students at http://localhost:5500/register.html

---

## Project Structure Overview

```
Online Examination System/
├── database/
│   ├── schema.sql              # Database schema
│   ├── sample_data.sql         # Sample data
│   └── ER_DIAGRAM.md          # ER diagram explanation
├── backend/
│   ├── src/main/java/com/exam/system/
│   │   ├── entity/            # JPA entities
│   │   ├── repository/        # Data access layer
│   │   ├── service/           # Business logic
│   │   ├── controller/        # REST controllers
│   │   ├── security/          # JWT security
│   │   └── config/            # Configuration
│   ├── pom.xml                # Maven dependencies
│   └── application.properties # Configuration
├── frontend/
│   ├── login.html             # Login page
│   ├── register.html          # Registration page
│   ├── admin/                 # Admin pages
│   ├── student/               # Student pages
│   ├── css/                   # Stylesheets
│   └── js/                    # JavaScript files
├── proctoring-service/
│   ├── app.py                 # Flask application
│   ├── modules/               # Proctoring modules
│   └── requirements.txt       # Python dependencies
└── README.md                  # This file
```

---

## Next Steps

1. ✅ Database setup complete
2. ✅ Backend running on port 8080
3. ✅ Frontend accessible on port 5500
4. ✅ Proctoring service running on port 5000
5. ✅ Test with demo credentials
6. ✅ Create new exams and questions
7. ✅ Take exams as student
8. ✅ Review proctoring logs

---

## Support

For issues or questions:
1. Check troubleshooting section
2. Review error logs in terminal
3. Verify all services are running
4. Check database connectivity

---

## Production Deployment Notes

### For Production Use:
1. Change all default passwords
2. Use environment variables for sensitive data
3. Enable HTTPS
4. Configure proper CORS origins
5. Add rate limiting
6. Implement proper logging
7. Set up database backups
8. Use production-grade servers (Tomcat, Nginx)
9. Add monitoring and alerts

---

**Project Created By**: Final Year Engineering Student  
**Technology Stack**: Java, Spring Boot, MySQL, HTML/CSS/JS, Python, OpenCV  
**Purpose**: Final Year Project - Online Examination System with AI-Based Proctoring
