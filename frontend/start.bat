@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul

echo Vue版我的电脑 - 快速启动
echo ================================
echo.

echo [1/3] 检查Node.js环境...
node -v >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到Node.js，请先安装Node.js 16+
    pause
    exit /b 1
)
echo [成功] Node.js版本: 
node -v

echo [2/3] 安装依赖...
if not exist node_modules (
    echo 正在安装依赖包...
    npm install
    if errorlevel 1 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
) else (
    echo [信息] 依赖已存在，跳过安装
)

echo [3/3] 启动开发服务器...
echo Vue应用地址: http://localhost:3000
echo 原Spring Boot后端: http://localhost:8080
echo 按 Ctrl+C 停止
echo.

npm run dev

echo.
echo 应用已停止
pause 