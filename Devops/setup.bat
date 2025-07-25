@echo off
echo ========================================
echo   DevOps Jenkins Dashboard Setup
echo ========================================
echo.

REM Check if Docker is running
docker --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Docker is not installed or not running!
    echo Please install Docker Desktop and make sure it's running.
    pause
    exit /b 1
)

echo ✓ Docker is available

REM Check if Jenkins is running on port 8080
curl -s http://localhost:8080 >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo.
    echo WARNING: Jenkins is not running on port 8080
    echo Please make sure Jenkins is installed and running on http://localhost:8080
    echo.
    echo To install Jenkins:
    echo 1. Download from: https://www.jenkins.io/download/
    echo 2. Install and start Jenkins
    echo 3. Access http://localhost:8080 and complete setup
    echo.
    set /p continue="Continue anyway? (y/n): "
    if /i not "%continue%"=="y" exit /b 1
)

echo ✓ Jenkins appears to be running

echo.
echo Building and starting services...
echo.

REM Build and start all services
docker-compose up -d --build

echo.
echo Waiting for services to start...
powershell -Command "Start-Sleep -Seconds 30"

echo.
echo ========================================
echo   Setup Complete!
echo ========================================
echo.
echo Services running:
echo • Frontend:  http://localhost:3000
echo • Backend:   http://localhost:8020
echo • Jenkins:   http://localhost:8080
echo • Grafana:   http://localhost:3001
echo • Prometheus: http://localhost:9090
echo.
echo Next steps:
echo 1. Open http://localhost:3000 in your browser
echo 2. Configure Jenkins credentials in the frontend
echo 3. Start monitoring your Jenkins jobs!
echo.
pause