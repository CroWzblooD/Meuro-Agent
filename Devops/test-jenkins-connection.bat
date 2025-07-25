@echo off
echo Testing Jenkins Integration...

echo.
echo 1. Testing Spring Boot Health...
curl -X GET http://localhost:8020/actuator/health
echo.

echo.
echo 2. Testing Jenkins Status...
curl -X GET http://localhost:8020/api/jenkins/status
echo.

echo.
echo 3. Fetching All Jenkins Jobs...
curl -X GET http://localhost:8020/api/jenkins/jobs
echo.

echo.
echo 4. Testing Job Trigger (success-job)...
curl -X POST http://localhost:8020/api/jenkins/trigger/success-job
echo.

echo.
echo 5. Testing Job Trigger (fail-job)...
curl -X POST http://localhost:8020/api/jenkins/trigger/fail-job
echo.

echo.
echo 6. Getting Job Details...
curl -X GET http://localhost:8020/api/jenkins/job/success-job
echo.

pause