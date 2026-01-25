@echo off
REM ============================================
REM Backend Server Only - Quick Start
REM ============================================

echo Starting Backend Server...

REM Set environment variables
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=online_exam_system
set DB_USERNAME=root
set DB_PASSWORD=965023
set JWT_SECRET=OnlineExamSystemSecretKeyForJWTTokenGenerationAndValidation2026!
set JWT_EXPIRATION=86400000
set SERVER_PORT=8081

cd backend
java -jar target\online-examination-system-1.0.0.jar
