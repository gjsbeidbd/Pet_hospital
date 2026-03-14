@echo off
title PetHospitalProject 启动器

echo ==========================================
echo   PetHospitalProject 启动器
echo ==========================================
echo.

:: 检查是否已经运行在Windows系统上
echo 检查系统环境...
echo.

:: 启动后端服务
echo 正在启动后端服务...
echo 后端服务将在新窗口中启动
start "后端服务 - PetHospitalProject" cmd /k "cd /d "%~dp0" && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

:: 启动前端服务
echo.
echo 正在启动前端服务...
echo 前端服务将在新窗口中启动
cd front
start "前端服务 - PetHospitalProject" cmd /k "cd /d "%~dp0front" && npm run dev"
cd ..

echo.
echo ==========================================
echo 启动完成！
echo ==========================================
echo 后端服务地址: http://localhost:8080
echo 前端服务地址: http://localhost:5180
echo.
echo 请等待服务完全启动后再访问相应地址
echo 按任意键退出此窗口...
pause >nul