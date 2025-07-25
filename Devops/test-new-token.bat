@echo off
echo Testing New Jenkins Token...
echo.

set /p JENKINS_USER="Enter your Jenkins username (e.g., crowblood): "
set /p JENKINS_TOKEN="Enter your new Jenkins API token: "

echo.
echo Testing connection to Jenkins...
curl -u "%JENKINS_USER%:%JENKINS_TOKEN%" "http://localhost:8080/api/json?tree=jobs[name,color]"

echo.
echo.
if %ERRORLEVEL% equ 0 (
    echo ✓ Token works! Now updating the application...
    echo.
    
    REM Update the Spring Boot configuration
    curl -X POST "http://localhost:8020/api/jenkins/config" ^
         -H "Content-Type: application/json" ^
         -d "{\"url\":\"http://host.docker.internal:8080\",\"user\":\"%JENKINS_USER%\",\"token\":\"%JENKINS_TOKEN%\",\"job\":\"test-job\"}"
    
    echo.
    echo.
    echo ✓ Configuration updated! Testing job fetch...
    curl -X GET "http://localhost:8020/api/jobs"
    
) else (
    echo ❌ Token authentication failed. Please check your username and token.
)

echo.
pause