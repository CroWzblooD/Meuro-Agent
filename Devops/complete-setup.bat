@echo off
echo ========================================
echo Complete Jenkins + Spring Boot Setup
echo ========================================

echo.
echo Step 1: Building Spring Boot Application...
cd springboot-backend
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo Build failed! Please check errors above.
    pause
    exit /b 1
)

echo.
echo Step 2: Building Docker Images...
cd ..
docker-compose build springboot-backend
if %ERRORLEVEL% neq 0 (
    echo Docker build failed!
    pause
    exit /b 1
)

echo.
echo Step 3: Starting Services...
docker-compose up -d mysql
timeout /t 10
docker-compose up -d springboot-backend
timeout /t 15

echo.
echo Step 4: Testing Spring Boot API...
curl -X GET http://localhost:8020/api/jenkins/jobs
echo.

echo.
echo Step 5: Testing Jenkins Connection...
curl -X GET http://localhost:8020/api/jenkins/status
echo.

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo Spring Boot: http://localhost:8020
echo Jenkins: http://localhost:8080
echo.
echo Next steps:
echo 1. Update jenkins.token in application.properties with your new token
echo 2. Run create-jenkins-jobs.bat to create test jobs
echo 3. Test the application
echo.
pause