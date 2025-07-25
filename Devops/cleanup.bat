@echo off
echo ========================================
echo   DevOps Dashboard Cleanup
echo ========================================
echo.

echo Stopping all services...
docker-compose down

echo Removing containers...
docker-compose down --volumes --remove-orphans

echo Cleaning up Docker images...
docker system prune -f

echo Removing build artifacts...
if exist "springboot-backend\target" rmdir /s /q "springboot-backend\target"
if exist "react-frontend\build" rmdir /s /q "react-frontend\build"
if exist "react-frontend\node_modules" rmdir /s /q "react-frontend\node_modules"

echo.
echo ✓ Cleanup complete!
echo.
echo To restart the project, run:
echo   setup.bat
echo.
pause