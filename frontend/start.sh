#!/bin/bash

echo "Vue版我的电脑 - 快速启动"
echo "================================"
echo

echo "[1/3] 检查Node.js环境..."
if ! command -v node &> /dev/null; then
    echo "[错误] 未找到Node.js，请先安装Node.js 16+"
    exit 1
fi
echo "[成功] Node.js版本: $(node -v)"

echo "[2/3] 安装依赖..."
if [ ! -d "node_modules" ]; then
    echo "正在安装依赖包..."
    npm install
    if [ $? -ne 0 ]; then
        echo "[错误] 依赖安装失败"
        exit 1
    fi
else
    echo "[信息] 依赖已存在，跳过安装"
fi

echo "[3/3] 启动开发服务器..."
echo "Vue应用地址: http://localhost:3000"
echo "原Spring Boot后端: http://localhost:8080"
echo "按 Ctrl+C 停止"
echo

npm run dev 