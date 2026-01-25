@echo off
echo ==========================================
echo   Online Examination System - GitHub Setup
echo ==========================================
echo.

echo [1/3] Initializing Git Repository...
git init

echo.
echo [2/3] Adding Files...
git add .

echo.
echo [3/3] Creating Initial Commit...
git commit -m "Initial Commit: Online Exam System with AI Proctoring - Complete V1.0"

echo.
echo ==========================================
echo   Status: READY TO PUSH
echo ==========================================
echo.
echo Next Steps:
echo 1. Go to https://github.com/new
echo 2. Create a repository named "online-exam-system"
echo 3. Run the following commands shown on GitHub:
echo    git remote add origin https://github.com/YOUR_USERNAME/online-exam-system.git
echo    git branch -M main
echo    git push -u origin main
echo.
pause
