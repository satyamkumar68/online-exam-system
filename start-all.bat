@echo off
REM ============================================
REM Online Examination System - Complete Startup
REM Starts Backend, Frontend, and Proctoring Service
REM ============================================

echo.
echo ================================================
echo   Online Examination System - Starting All Services
echo ================================================
echo.

REM Set environment variables for backend
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=online_exam_system
set DB_USERNAME=root
set DB_PASSWORD=965023
set JWT_SECRET=OnlineExamSystemSecretKeyForJWTTokenGenerationAndValidation2026!
set JWT_EXPIRATION=86400000
set SERVER_PORT=8081
set CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5500,http://127.0.0.1:5500
set PROCTORING_SERVICE_URL=http://localhost:5000
set APP_NAME=Online Examination System
set APP_VERSION=1.0.0
set ENVIRONMENT=development
set SHOW_SQL=true

echo [1/3] Starting Backend Server (Spring Boot)...
echo      Port: 8081
echo      Database: %DB_NAME%
echo.

REM Start backend in new window
start "Backend Server" cmd /k "cd backend && java -jar target\online-examination-system-1.0.0.jar"

echo Waiting for backend to initialize...
timeout /t 10 /nobreak > nul

echo.
echo [2/3] Starting Frontend Server (HTTP)...
echo      Port: 5500
echo      URL: http://localhost:5500
echo.

REM Start frontend in new window
start "Frontend Server" cmd /k "cd frontend && python -m http.server 5500"

timeout /t 2 /nobreak > nul

echo.
echo [3/3] Starting AI Proctoring Service (Flask)...
echo      Port: 5000
echo.

REM Start proctoring service in new window
start "Proctoring Service" cmd /k "cd proctoring-service && python app.py"

timeout /t 3 /nobreak > nul

echo.
echo ================================================
echo   All Services Started Successfully!
echo ================================================
echo.
echo   Backend API:       http://localhost:8081/api
echo   Frontend:          http://localhost:5500
echo   Proctoring:        http://localhost:5000
echo.
echo   Default Admin Login:
echo   Username: admin
echo   Password: password123
echo.
echo   Default Student Login:
echo   Username: john.doe
echo   Password: password123
echo.
echo ================================================
echo.
echo Opening browser in 5 seconds...
timeout /t 5 /nobreak > nul

REM Open browser to login page
start http://localhost:5500/login.html

echo.
echo Browser opened! You can now test the application.
echo.
echo Press any key to stop all services...
pause > nul

echo.
echo Stopping all services...
taskkill /FI "WINDOWTITLE eq Backend Server*" /T /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Frontend Server*" /T /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Proctoring Service*" /T /F > nul 2>&1

echo All services stopped.
echo.
