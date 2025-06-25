 @echo off
setlocal enabledelayedexpansion
chcp 65001 >nul

echo 家庭媒体共享中心 - 快速重启
echo ================================
echo.

set PORT=8080
set MAVEN_PATH="D:\Program Files\apache-maven-3.8.8-bin\apache-maven-3.8.8\bin\mvn.cmd"

echo [1/4] 检查端口占用...
netstat -ano | findstr :%PORT% >nul
if %errorlevel% equ 0 (
    echo [警告] 端口 %PORT% 被占用，正在终止进程...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%PORT%') do (
        taskkill /PID %%a /F >nul 2>&1
        echo [成功] 进程已终止 (PID: %%a)
    )
    timeout /t 2 /nobreak >nul
) else (
    echo [信息] 端口 %PORT% 未被占用
)

echo [2/4] 检查环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到Java
    pause
    exit /b 1
)

if exist %MAVEN_PATH% (
    set MAVEN_CMD=%MAVEN_PATH%
) else (
    mvn -version >nul 2>&1
    if errorlevel 1 (
        echo [错误] 未找到Maven
        pause
        exit /b 1
    ) else (
        set MAVEN_CMD=mvn
    )
)

echo [3/4] 编译项目...
call %MAVEN_CMD% clean compile -q
if errorlevel 1 (
    echo [错误] 编译失败
    pause
    exit /b 1
)

echo [4/4] 启动应用...
echo 应用地址: http://localhost:%PORT%
echo 按 Ctrl+C 停止
echo.

call %MAVEN_CMD% spring-boot:run

echo.
echo 应用已停止
pause 