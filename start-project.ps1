# PetHospitalProject 启动脚本
Write-Host "==========================================" -ForegroundColor Green
Write-Host "  PetHospitalProject 启动器" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""

# 检查是否安装了必要的工具
Write-Host "检查系统环境..." -ForegroundColor Yellow
Write-Host ""

# 检查Java是否安装
try {
    $javaVersion = java -version 2>&1
    Write-Host "✓ Java 已安装" -ForegroundColor Green
} catch {
    Write-Host "✗ 未找到 Java，请先安装 Java 17 或更高版本" -ForegroundColor Red
    pause
    exit 1
}

# 检查Maven是否安装
try {
    $mvnVersion = mvn -version 2>&1
    Write-Host "✓ Maven 已安装" -ForegroundColor Green
} catch {
    Write-Host "✗ 未找到 Maven，请先安装 Maven" -ForegroundColor Red
    pause
    exit 1
}

# 检查Node.js是否安装
try {
    $nodeVersion = node --version 2>&1
    Write-Host "✓ Node.js 已安装 ($nodeVersion)" -ForegroundColor Green
} catch {
    Write-Host "✗ 未找到 Node.js，请先安装 Node.js" -ForegroundColor Red
    pause
    exit 1
}

# 检查npm是否安装
try {
    $npmVersion = npm --version 2>&1
    Write-Host "✓ npm 已安装 ($npmVersion)" -ForegroundColor Green
} catch {
    Write-Host "✗ 未找到 npm，请先安装 Node.js (包含 npm)" -ForegroundColor Red
    pause
    exit 1
}

Write-Host ""
Write-Host "环境检查完成！" -ForegroundColor Green
Write-Host ""

# 启动后端服务
Write-Host "正在启动后端服务..." -ForegroundColor Yellow
Write-Host "后端服务将在新窗口中启动" -ForegroundColor Gray

Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PWD'; mvn spring-boot:run" -Verb Open -WindowStyle Normal -PassThru | Out-Null

Write-Host "✓ 后端服务启动命令已执行" -ForegroundColor Green
Write-Host ""

# 等待后端服务启动
Write-Host "等待后端服务启动 (预计需要10-15秒)..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 启动前端服务
Write-Host "正在启动前端服务..." -ForegroundColor Yellow
Write-Host "前端服务将在新窗口中启动" -ForegroundColor Gray

Set-Location front
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PWD'; npm run dev" -Verb Open -WindowStyle Normal -PassThru | Out-Null
Set-Location ..

Write-Host "✓ 前端服务启动命令已执行" -ForegroundColor Green
Write-Host ""

Write-Host "==========================================" -ForegroundColor Green
Write-Host "启动完成！" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host "后端服务地址: http://localhost:8080" -ForegroundColor Cyan
Write-Host "前端服务地址: http://localhost:5180" -ForegroundColor Cyan
Write-Host ""
Write-Host "请等待服务完全启动后再访问相应地址" -ForegroundColor Yellow
Write-Host "后端服务完全启动后，您会看到 'Started PetHospitalProjectApplication' 的日志信息" -ForegroundColor Gray
Write-Host ""

Write-Host "按任意键退出此窗口..." -ForegroundColor Gray
$host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")