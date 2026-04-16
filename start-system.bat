@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

echo.
echo ======================================
echo   Job Interview System - Quick Start
echo ======================================
echo.

REM Check Java
echo [1/3] Checking Java...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found!
    pause
    exit /b 1
)
echo OK: Java installed

REM Check Maven
echo [2/3] Checking Maven...
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven not found!
    pause
    exit /b 1
)
echo OK: Maven installed

REM Check Node.js
echo [3/3] Checking Node.js...
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js not found!
    pause
    exit /b 1
)
echo OK: Node.js installed

echo.
echo Cleaning ports...
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM node.exe >nul 2>&1
timeout /t 2 /nobreak >nul

echo.
echo ======================================
echo   Starting Services
echo ======================================
echo.

REM Start Backend
echo Starting Backend on port 8081...
cd /d "%SCRIPT_DIR%backend"
start "Backend-8081" cmd /k "title Backend-8081 && mvn spring-boot:run"

timeout /t 5 /nobreak >nul

REM Start Frontend
echo Starting Frontend on port 8080...
cd /d "%SCRIPT_DIR%frontend"

if not exist "node_modules" (
    echo Installing frontend dependencies...
    call npm install
)

start "Frontend-8080" cmd /k "title Frontend-8080 && npm run serve"

cd /d "%SCRIPT_DIR%"

echo.
echo ======================================
echo   Services Starting...
echo ======================================
echo.
echo Please wait 30-60 seconds for services to start
echo.
echo Access: http://localhost:8080
echo.
echo Test accounts:
echo   Student: student / 123456
echo   Teacher: teacher / 123456
echo.
echo Backend ready: Started InterviewSystemApplication
echo Frontend ready: App running at: http://localhost:8080
echo.
echo ======================================
echo.

pause
