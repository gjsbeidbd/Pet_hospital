@echo off
title PetHospitalProject 开发环境停止器

echo ==========================================
echo   PetHospitalProject 开发环境停止器
echo ==========================================
echo.

echo 正在停止开发环境...
echo.

echo 停止前端服务 (端口: 5180)
taskkill /f /im node.exe /t >nul 2>&1

echo 停止后端服务 (端口: 8080)
taskkill /f /im java.exe /t >nul 2>&1

echo.
echo ==========================================
echo 开发环境已停止
echo ==========================================
echo.
echo 按任意键关闭此窗口...
pause >nul