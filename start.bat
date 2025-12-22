@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM ========================================
REM   就业面试辅助系统 - 启动脚本
REM   版本: v3.0
REM ========================================

REM 获取脚本所在目录
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

REM 配置 Maven Daemon 路径
set "MVND_HOME=D:\program\maven-mvnd-1.0.3-windows-amd64\maven-mvnd-1.0.3-windows-amd64\maven-mvnd-1.0.3-windows-amd64"
set "MVND_CMD=%MVND_HOME%\bin\mvnd.cmd"

REM 检查 Maven 是否可用
set "MVN_AVAILABLE=0"
if exist "%MVND_CMD%" (
    set "MVN_AVAILABLE=1"
    set "MVN_CMD=%MVND_CMD%"
) else (
    where mvn >nul 2>&1
    if !errorlevel! equ 0 (
        set "MVN_AVAILABLE=1"
        set "MVN_CMD=mvn"
    )
)

:menu
cls
echo.
echo  ======================================
echo       就业面试辅助系统 - 启动脚本
echo  ======================================
echo   后端端口: 8081
echo   前端端口: 8080
echo   访问地址: http://localhost:8080
echo  --------------------------------------
echo   测试账号:
echo     学生: student / 123456
echo     教师: teacher / 123456
echo  ======================================
echo.
echo  [1] 启动后端服务
echo  [2] 启动前端服务
echo  [3] 同时启动前后端 (推荐)
echo  [4] 检查环境配置
echo  [5] 清理端口占用
echo  [6] 清理项目构建
echo  [0] 退出
echo.
set /p choice=请选择 [0-6]: 

if "%choice%"=="1" goto start_backend
if "%choice%"=="2" goto start_frontend
if "%choice%"=="3" goto start_both
if "%choice%"=="4" goto check_env
if "%choice%"=="5" goto clean_ports
if "%choice%"=="6" goto clean_project
if "%choice%"=="0" goto end
goto menu

:check_env
cls
echo.
echo  ========== 环境检查 ==========
echo.

REM 检查 Java
echo  [检查] Java...
where java >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VER=%%i
    echo         OK Java 已安装: !JAVA_VER!
) else (
    echo         FAIL Java 未安装或未配置PATH
)

REM 检查 Maven
echo  [检查] Maven...
if "%MVN_AVAILABLE%"=="1" (
    echo         OK Maven 可用: %MVN_CMD%
) else (
    echo         FAIL Maven 未找到
    echo           期望路径: %MVND_CMD%
)

REM 检查 Node.js
echo  [检查] Node.js...
where node >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=*" %%i in ('node -v') do set NODE_VER=%%i
    echo         OK Node.js 已安装: !NODE_VER!
) else (
    echo         FAIL Node.js 未安装或未配置PATH
)

REM 检查 npm
echo  [检查] npm...
where npm >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=*" %%i in ('npm -v') do set NPM_VER=%%i
    echo         OK npm 已安装: !NPM_VER!
) else (
    echo         FAIL npm 未安装或未配置PATH
)

REM 检查项目目录
echo  [检查] 项目目录...
if exist "%SCRIPT_DIR%backend\pom.xml" (
    echo         OK 后端项目存在
) else (
    echo         FAIL 后端项目不存在
)
if exist "%SCRIPT_DIR%frontend\package.json" (
    echo         OK 前端项目存在
) else (
    echo         FAIL 前端项目不存在
)

echo.
echo  ================================
echo.
pause
goto menu

:start_backend
cls
echo.
echo  正在启动后端服务...
echo.

REM 清理8081端口
call :kill_port 8081
echo.

if "%MVN_AVAILABLE%"=="0" (
    echo  [错误] Maven 未找到！
    echo.
    echo  请确认以下路径是否正确:
    echo  %MVND_CMD%
    echo.
    echo  或者安装 Maven 并配置环境变量
    echo.
    pause
    goto menu
)

if not exist "%SCRIPT_DIR%backend\pom.xml" (
    echo  [错误] 后端项目不存在！
    pause
    goto menu
)

echo  使用: %MVN_CMD%
echo.

cd /d "%SCRIPT_DIR%backend"
start "后端服务 [8081]" cmd /k "title 后端服务 [端口:8081] && echo. && echo 正在启动后端服务，请稍候... && echo. && call "%MVN_CMD%" spring-boot:run"

echo  后端服务已在新窗口启动！
echo  API地址: http://localhost:8081/api
echo.
cd /d "%SCRIPT_DIR%"
pause
goto menu

:start_frontend
cls
echo.
echo  正在启动前端服务...
echo.

REM 清理8080端口
call :kill_port 8080
echo.

where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo  [错误] npm 未找到！请安装 Node.js
    pause
    goto menu
)

if not exist "%SCRIPT_DIR%frontend\package.json" (
    echo  [错误] 前端项目不存在！
    pause
    goto menu
)

cd /d "%SCRIPT_DIR%frontend"

REM 检查 node_modules
if not exist "node_modules" (
    echo  [提示] 首次运行，正在安装依赖...
    echo.
    call npm install
    if %errorlevel% neq 0 (
        echo  [错误] 依赖安装失败！
        pause
        goto menu
    )
)

start "前端服务 [8080]" cmd /k "title 前端服务 [端口:8080] && echo. && echo 正在启动前端服务... && echo. && npm run serve"

echo  前端服务已在新窗口启动！
echo  访问地址: http://localhost:8080
echo.
cd /d "%SCRIPT_DIR%"
pause
goto menu

:start_both
cls
echo.
echo  正在同时启动前后端服务...
echo.

REM 清理端口
echo  [准备] 清理端口占用...
call :kill_port 8080
call :kill_port 8081
echo.

REM 检查 Maven
if "%MVN_AVAILABLE%"=="0" (
    echo  [错误] Maven 未找到！
    echo  请运行选项 4 检查环境配置
    pause
    goto menu
)

REM 检查 npm
where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo  [错误] npm 未找到！请安装 Node.js
    pause
    goto menu
)

REM 启动后端
echo  [1/2] 启动后端服务...
cd /d "%SCRIPT_DIR%backend"
start "后端服务 [8081]" cmd /k "title 后端服务 [端口:8081] && echo. && echo 正在启动后端服务，请稍候... && echo. && call "%MVN_CMD%" spring-boot:run"

REM 等待一下
timeout /t 2 /nobreak >nul

REM 启动前端
echo  [2/2] 启动前端服务...
cd /d "%SCRIPT_DIR%frontend"

if not exist "node_modules" (
    echo  [提示] 首次运行，正在安装前端依赖...
    call npm install
)

start "前端服务 [8080]" cmd /k "title 前端服务 [端口:8080] && echo. && echo 正在启动前端服务... && echo. && npm run serve"

cd /d "%SCRIPT_DIR%"

echo.
echo  ======================================
echo    前后端服务已启动！
echo  --------------------------------------
echo    请等待约 30-60 秒服务启动完成
echo.
echo    后端就绪标志:
echo      Started InterviewSystemApplication
echo.
echo    前端就绪标志:
echo      App running at: http://localhost:8080
echo  ======================================
echo.
pause
goto menu

:clean_ports
cls
echo.
echo  ========== 清理端口占用 ==========
echo.
echo  正在清理端口 8080 和 8081...
echo.
call :kill_port 8080
call :kill_port 8081
echo.
echo  端口清理完成！
echo.
pause
goto menu

:clean_project
cls
echo.
echo  ========== 清理项目构建 ==========
echo.
echo  警告: 此操作将删除以下内容:
echo    - backend/target 目录
echo    - frontend/node_modules 目录
echo    - frontend/dist 目录
echo.
set /p confirm=确认清理? (Y/N): 
if /i not "%confirm%"=="Y" (
    echo  已取消清理
    pause
    goto menu
)

echo.
echo  [1/3] 清理后端构建...
if exist "%SCRIPT_DIR%backend\target" (
    rd /s /q "%SCRIPT_DIR%backend\target"
    echo        OK 已删除 backend/target
) else (
    echo        - backend/target 不存在
)

echo  [2/3] 清理前端构建...
if exist "%SCRIPT_DIR%frontend\dist" (
    rd /s /q "%SCRIPT_DIR%frontend\dist"
    echo        OK 已删除 frontend/dist
) else (
    echo        - frontend/dist 不存在
)

echo  [3/3] 清理前端依赖...
if exist "%SCRIPT_DIR%frontend\node_modules" (
    echo        正在删除 node_modules (可能需要较长时间)...
    rd /s /q "%SCRIPT_DIR%frontend\node_modules"
    echo        OK 已删除 frontend/node_modules
) else (
    echo        - frontend/node_modules 不存在
)

echo.
echo  清理完成！
echo.
pause
goto menu

:kill_port
REM 参数: %1 = 端口号
set "KILL_PORT=%~1"
echo  [清理] 检查端口 %KILL_PORT%...

REM 直接使用taskkill终止Java和Node进程
if "%KILL_PORT%"=="8081" (
    taskkill /F /IM java.exe >nul 2>&1
    if !errorlevel! equ 0 (
        echo        OK 已终止Java进程
    ) else (
        echo        - 无Java进程运行
    )
)
if "%KILL_PORT%"=="8080" (
    taskkill /F /IM node.exe >nul 2>&1
    if !errorlevel! equ 0 (
        echo        OK 已终止Node进程
    ) else (
        echo        - 无Node进程运行
    )
)

REM 等待进程完全终止
timeout /t 2 /nobreak >nul
goto :eof

:end
echo.
echo  感谢使用！
echo.
exit /b 0
