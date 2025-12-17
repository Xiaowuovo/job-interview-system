@echo off
chcp 65001 >nul

echo ========================================
echo   就业面试辅助系统
echo ========================================
echo.
echo 1. 启动后端
echo 2. 启动前端
echo 3. 同时启动
echo.
set /p choice=选择 (1/2/3):

if "%choice%"=="1" (
    echo 启动后端...
    cd backend
    powershell -Command "mvnd spring-boot:run"
) else if "%choice%"=="2" (
    echo 启动前端...
    cd frontend
    cmd /k npm run serve
) else if "%choice%"=="3" (
    echo 同时启动...
    start "后端" powershell -NoExit -Command "cd '%~dp0backend'; mvnd spring-boot:run"
    timeout /t 3 >nul
    start "前端" cmd /k "cd /d %~dp0frontend && npm run serve"
    echo.
    echo 已启动！
    echo.
    echo 前端: http://localhost:8080
    echo 后端: http://localhost:8081/api
    echo.
    echo 账号: student/123456 或 teacher/123456
    echo.
    pause
)