#!/bin/bash

echo "========================================"
echo "   DevOps Jenkins Dashboard Setup"
echo "========================================"
echo

# Check if Docker is running
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed!"
    echo "Please install Docker and make sure it's running."
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "ERROR: Docker is not running!"
    echo "Please start Docker and try again."
    exit 1
fi

echo "✓ Docker is available"

# Check if Jenkins is running on port 8080
if ! curl -s http://localhost:8080 &> /dev/null; then
    echo
    echo "WARNING: Jenkins is not running on port 8080"
    echo "Please make sure Jenkins is installed and running on http://localhost:8080"
    echo
    echo "To install Jenkins on macOS:"
    echo "1. brew install jenkins-lts"
    echo "2. brew services start jenkins-lts"
    echo "3. Access http://localhost:8080 and complete setup"
    echo
    read -p "Continue anyway? (y/n): " continue
    if [[ ! "$continue" =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo "✓ Jenkins appears to be running"

echo
echo "Building and starting services..."
echo

# Build and start all services
docker-compose up -d --build

echo
echo "Waiting for services to start..."
sleep 30

echo
echo "========================================"
echo "   Setup Complete!"
echo "========================================"
echo
echo "Services running:"
echo "• Frontend:  http://localhost:3000"
echo "• Backend:   http://localhost:8020"
echo "• Jenkins:   http://localhost:8080"
echo "• Grafana:   http://localhost:3001"
echo "• Prometheus: http://localhost:9090"
echo
echo "Next steps:"
echo "1. Open http://localhost:3000 in your browser"
echo "2. Configure Jenkins credentials in the frontend"
echo "3. Start monitoring your Jenkins jobs!"
echo