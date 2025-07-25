# DevOps Jenkins Dashboard

A comprehensive DevOps monitoring dashboard that integrates Jenkins, Spring Boot, React, and machine learning for intelligent job monitoring and anomaly detection.

## 🚀 Quick Setup

### Prerequisites

- **Docker & Docker Compose** (required)
- **Jenkins** running on `http://localhost:8080` (required)
- **Git** (for cloning)

### One-Command Setup

#### Windows:

```bash
git clone <your-repo-url>
cd Devops
./setup.bat
```

#### macOS/Linux:

```bash
git clone <your-repo-url>
cd Devops
chmod +x setup.sh
./setup.sh
```

That's it! 🎉

## 📋 What Gets Installed

The setup script will automatically:

1. ✅ Check Docker installation
2. ✅ Verify Jenkins is running
3. ✅ Build all Docker containers
4. ✅ Start all services
5. ✅ Configure networking

## 🌐 Access Your Dashboard

After setup completes, access these URLs:

| Service            | URL                   | Description                             |
| ------------------ | --------------------- | --------------------------------------- |
| **Main Dashboard** | http://localhost:3000 | React frontend with Jenkins integration |
| **Backend API**    | http://localhost:8020 | Spring Boot REST API                    |
| **Jenkins**        | http://localhost:8080 | Your Jenkins instance                   |
| **Grafana**        | http://localhost:3001 | Metrics visualization                   |
| **Prometheus**     | http://localhost:9090 | Metrics collection                      |

## ⚙️ Configure Jenkins Connection

1. **Open the Dashboard**: Go to http://localhost:3000
2. **Click "Configure Jenkins"** button
3. **Enter your credentials**:
   - **Jenkins URL**: `http://localhost:8080` (default)
   - **Username**: Your Jenkins username
   - **API Token**: Generate from Jenkins → User → Configure → API Token
4. **Click "Save & Test Configuration"**
5. **Start monitoring your jobs!** 🎯

## 🔧 Jenkins API Token Setup

### Getting Your Jenkins API Token:

1. Open Jenkins: http://localhost:8080
2. Click your username (top right)
3. Click "Configure"
4. Scroll to "API Token" section
5. Click "Add new Token"
6. Give it a name (e.g., "Dashboard-Token")
7. Click "Generate"
8. **Copy the token immediately** (you won't see it again!)
9. Paste it in the dashboard configuration

## 📊 Features

### 🎯 Core Features

- **Real-time Jenkins job monitoring**
- **Dynamic credential configuration**
- **Job status tracking (SUCCESS/FAILURE)**
- **Historical job data**
- **Anomaly detection with ML**
- **Responsive web interface**

### 🤖 Advanced Features

- **Machine Learning anomaly detection**
- **Prometheus metrics integration**
- **Grafana dashboards**
- **Docker containerization**
- **Cross-platform support**

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │  Spring Boot    │    │     Jenkins     │
│   (Port 3000)   │◄──►│   Backend       │◄──►│   (Port 8080)   │
│                 │    │   (Port 8020)   │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     Grafana     │    │   Prometheus    │    │   ML Service    │
│   (Port 3001)   │    │   (Port 9090)   │    │   (Port 5000)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Development

### Project Structure

```
Devops/
├── setup.bat/setup.sh          # One-command setup scripts
├── docker-compose.yml          # Container orchestration
├── springboot-backend/         # Java Spring Boot API
├── react-frontend/             # React.js dashboard
├── ml-service/                 # Python ML service
├── kubernetes/                 # K8s deployment files
└── ansible/                    # Automation scripts
```

### Manual Development Setup

```bash
# Backend
cd springboot-backend
./mvnw spring-boot:run

# Frontend
cd react-frontend
npm install
npm start

# ML Service
cd ml-service
pip install -r requirements.txt
python app.py
```

## 🐳 Docker Services

| Service      | Container          | Port | Purpose           |
| ------------ | ------------------ | ---- | ----------------- |
| Frontend     | react-frontend     | 3000 | Web UI            |
| Backend      | springboot-backend | 8020 | REST API          |
| Database     | mysqldbs           | 3307 | Data storage      |
| ML Service   | ml-service         | 5000 | Anomaly detection |
| Prometheus   | prometheus         | 9090 | Metrics           |
| Grafana      | grafana            | 3001 | Dashboards        |
| Alertmanager | alertmanager       | 9093 | Alerts            |

## 🔍 Troubleshooting

### Common Issues

**❌ "Jenkins not running on port 8080"**

- Install Jenkins: https://www.jenkins.io/download/
- Start Jenkins service
- Verify: http://localhost:8080

**❌ "Docker not found"**

- Install Docker Desktop
- Start Docker service
- Verify: `docker --version`

**❌ "Port already in use"**

- Stop conflicting services
- Or modify ports in `docker-compose.yml`

**❌ "401 Unauthorized"**

- Check Jenkins username/token
- Regenerate API token if needed
- Verify Jenkins user permissions

### Logs & Debugging

```bash
# View all service logs
docker-compose logs

# View specific service logs
docker-compose logs springboot-backend
docker-compose logs react-frontend

# Restart services
docker-compose restart

# Rebuild and restart
docker-compose up -d --build
```

## 🚦 Testing Your Setup

### 1. Create Test Jobs in Jenkins

```bash
# Create a success job
curl -X POST "http://localhost:8080/createItem?name=test-success" \
  --user "username:token" \
  --header "Content-Type: application/xml" \
  --data '<project><builders><hudson.tasks.Shell><command>echo "Success!"; exit 0</command></hudson.tasks.Shell></builders></project>'

# Create a failure job
curl -X POST "http://localhost:8080/createItem?name=test-failure" \
  --user "username:token" \
  --header "Content-Type: application/xml" \
  --data '<project><builders><hudson.tasks.Shell><command>echo "Failure!"; exit 1</command></hudson.tasks.Shell></builders></project>'
```

### 2. Run Jobs and Monitor

1. Trigger jobs in Jenkins
2. Refresh dashboard to see results
3. Check job details and anomaly detection

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 🆘 Support

- **Issues**: Create GitHub issues for bugs
- **Questions**: Use GitHub discussions
- **Documentation**: Check this README first

---

**Made with ❤️ for DevOps Engineers**

_Happy Monitoring! 🚀_
