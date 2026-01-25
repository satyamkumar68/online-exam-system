@echo off
REM ============================================
REM Stop All Services
REM ============================================

echo Stopping all Online Examination System services...

taskkill /FI "WINDOWTITLE eq Backend Server*" /T /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Frontend Server*" /T /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Proctoring Service*" /T /F > nul 2>&1

REM Also kill by port
for /f "tokens=5" %%a in ('netstat -aon ^| find ":8081" ^| find "LISTENING"') do taskkill /F /PID %%a > nul 2>&1
for /f "tokens=5" %%a in ('netstat -aon ^| find ":5500" ^| find "LISTENING"') do taskkill /F /PID %%a > nul 2>&1
for /f "tokens=5" %%a in ('netstat -aon ^| find ":5000" ^| find "LISTENING"') do taskkill /F /PID %%a > nul 2>&1

echo All services stopped.
pause
