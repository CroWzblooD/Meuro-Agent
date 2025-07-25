#!/bin/bash

echo "========================================"
echo "   DevOps Dashboard Cleanup"
echo "========================================"
echo

echo "Stopping all services..."
docker-compose down

echo "Removing containers..."
docker-compose down --volumes --remove-orphans

echo "Cleaning up Docker images..."
docker system prune -f

echo "Removing build artifacts..."
rm -rf springboot-backend/target
rm -rf react-frontend/build
rm -rf react-frontend/node_modules

echo
echo "✓ Cleanup complete!"
echo
echo "To restart the project, run:"
echo "  ./setup.sh"
echo