@echo off
title PetHospitalProject 开发环境启动器

echo ==========================================
echo   PetHospitalProject 开发环境启动器
echo ==========================================
echo.

echo 检查并停止可能正在运行的服务...
echo.

echo 停止前端服务 (端口: 5180)
taskkill /f /im node.exe /t >nul 2>&1

echo 停止后端服务 (端口: 8080)
taskkill /f /im java.exe /t >nul 2>&1

echo.
echo 正在启动开发环境...
echo.

echo 启动后端服务 (端口: 8080)
start "后端服务" cmd /k "cd /d D:\File\PetHospital\PetHospitalProject && mvn spring-boot:run"

timeout /t 5 /nobreak >nul

echo.
echo 启动前端服务 (端口: 5180)
cd front
start "前端服务" cmd /k "cd /d D:\File\PetHospital\PetHospitalProject\front && npm run dev"
cd ..

echo.
echo ==========================================
echo 开发环境启动命令已执行
echo ==========================================
echo 后端服务地址: http://localhost:8080
echo 前端服务地址: http://localhost:5180
echo.
echo 请等待服务完全启动后再访问
echo 按任意键关闭此窗口...
pause >nul