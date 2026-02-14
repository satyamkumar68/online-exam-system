@echo off
echo ============================================
echo Starting Online Examination System (Rewritten)
echo ============================================

echo.
echo 1. Starting Backend (Port 8081)...
start "Backend Server" cmd /k "cd backend && mvn spring-boot:run"

echo.
echo 2. Starting Frontend (Port 7001)...
start "Frontend Server" cmd /k "cd frontend && python -m http.server 7001"

echo.
echo 3. Starting Proctoring Service (Port 7003)...
start "Proctoring Service" cmd /k "cd proctoring-service && python app.py"

echo.
echo ============================================
echo All services are starting...
echo.
echo Access the application at: http://127.0.0.1:7001
echo.
echo Backend API: http://127.0.0.1:8081/api
echo Proctoring API: http://127.0.0.1:7003
echo ============================================
pause
