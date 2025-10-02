# 🚀 Meuro Agent

<div align="center">

![DevOps](https://img.shields.io/badge/DevOps-Intelligence-blue?style=for-the-badge&logo=devdotto)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18+-blue?style=for-the-badge&logo=react)
![Python](https://img.shields.io/badge/Python-3.10+-yellow?style=for-the-badge&logo=python)
![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-orange?style=for-the-badge&logo=prometheus)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-orange?style=for-the-badge&logo=grafana)
![Slack](https://img.shields.io/badge/Slack-Alerts-4A154B?style=for-the-badge&logo=slack)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)

**Unified, intelligent, and actionable DevOps monitoring for modern CI/CD pipelines.**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=for-the-badge)](http://makeapullrequest.com)

</div>

---

## 📋 Table of Contents

- [🎯 Problem Statement](#-problem-statement)
- [💡 Solution Overview](#-solution-overview)
- [✨ Key Features](#-key-features)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Tech Stack](#️-tech-stack)
- [⚙️ Installation & Setup](#-installation--setup)
- [🎮 Usage Guide & Workflow](#-usage-guide--workflow)
- [🔬 Technical Details](#-technical-details)
- [📊 Monitoring, Alerting & Observability](#-monitoring-alerting--observability)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [🙏 Acknowledgments](#-acknowledgments)
- [📞 Support](#-support)

---

## 🎯 Problem Statement

Modern DevOps teams face:
- **Blind spots in CI/CD pipelines**: Failures and slowdowns are often detected too late.
- **Manual root cause analysis**: Engineers spend hours digging through logs and metrics.
- **Lack of actionable insights**: Dashboards show data, not solutions.
- **Fragmented observability**: Metrics, logs, and alerts are scattered across tools.
- **No intelligence**: Static thresholds and rules miss real anomalies.

---

## 💡 Solution Overview

**DevOps Intelligence Dashboard** is a plug-and-play platform that:
- Monitors Jenkins jobs in real time.
- Detects anomalies using ML and LLMs (Groq).
- Generates actionable, human-readable insights.
- Visualizes everything in Grafana.
- Sends alerts to Slack for instant action.
- Provides full traceability and observability for every job.

---

## ✨ Key Features

- **Live Jenkins Job Monitoring**: Status, duration, history, and trends.
- **ML Anomaly Detection**: Flags failures and outliers, even for intentional test failures.
- **LLM Insights (Groq)**: Explains root causes and next steps in plain English.
- **Prometheus Metrics**: Exposes all job and anomaly data for scraping.
- **Grafana Dashboards**: Beautiful, real-time visualizations.
- **Slack Alerts**: Instant notifications for every anomaly or failure.
- **Full Logging**: Every action is logged for audit and debugging.
- **One-Command Docker Deployment**: All services, one stack.

---

## 🏗️ Architecture

```
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│  Frontend    │      │  Backend     │      │  Jenkins     │
│  (React)     │◄────►│  (SpringBoot)│◄────►│  Server      │
└─────▲────────┘      └─────▲────────┘      └─────▲────────┘
      │                        │                     │
      │                        │                     │
      │                        ▼                     │
      │                ┌──────────────┐              │
      │                │  ML Service  │              │
      │                │  (Python)    │              │
      │                └─────▲────────┘              │
      │                      │                       │
      │                      ▼                       │
      │                ┌──────────────┐              │
      │                │  Groq LLM    │              │
      │                └─────▲────────┘              │
      │                      │                       │
      │                      ▼                       │
      │                ┌──────────────┐              │
      │                │ Prometheus   │◄─────────────┘
      │                └─────▲────────┘
      │                      │
      │                      ▼
      │                ┌──────────────┐
      │                │  Grafana     │
      │                └─────▲────────┘
      │                      │
      │                      ▼
      │                ┌──────────────┐
      │                │  Slack       │
      │                └──────────────┘
```

---

## 🛠️ Tech Stack

| Layer         | Technology         | Purpose/Role                                 |
|---------------|--------------------|----------------------------------------------|
| Frontend      | React, MUI, Axios  | UI, job polling, insights display            |
| Backend       | Spring Boot, Java  | API, Jenkins integration, metrics, LLM, ML   |
| ML Service    | Python, Flask, scikit-learn | Anomaly detection                  |
| LLM           | Groq API           | Human-readable insights                      |
| CI/CD         | Jenkins            | Job execution, build/test/deploy             |
| Monitoring    | Prometheus         | Metrics scraping                             |
| Visualization | Grafana            | Dashboards, alert visualization              |
| Alerting      | Alertmanager, Slack| Real-time notifications                      |
| Orchestration | Docker Compose     | One-command deployment                       |

---

## ⚙️ Installation & Setup

### **Prerequisites**
- Docker & Docker Compose
- Jenkins server (with jobs configured)
- Slack webhook URL
- Groq API key (for LLM insights)

### **1. Clone the Repository**
```bash
git clone https://github.com/yourusername/devops-intelligence-dashboard.git
cd devops-intelligence-dashboard
```

### **2. Configure Environment**
- Edit `springboot-backend/src/main/resources/application.properties` for Jenkins and ML URLs.
- Set your Slack webhook in `alertmanager.yml`.
- Add your Groq API key in `JenkinsService.java`.

### **3. Start All Services**
```bash
docker compose up --build
```

### **4. Access the Platform**
- **Frontend**: http://localhost:3000
- **Grafana**: http://localhost:3001
- **Prometheus**: http://localhost:9090
- **Jenkins**: http://localhost:8080

---

## 🎮 Usage Guide & Workflow

### **Typical Workflow**

1. **Run a Jenkins job** (success or fail).
2. **Dashboard polls jobs** (click "POLL ALL JOBS").
3. **Backend fetches job data, logs every step**.
4. **Backend calls ML service** with durations/statuses.
5. **ML service flags any failure as anomaly, logs request/response**.
6. **Backend calls Groq LLM for insight, logs prompt/response**.
7. **Backend exposes metrics for Prometheus**.
8. **Prometheus scrapes metrics**.
9. **Alertmanager sends Slack alert for anomaly/failure**.
10. **Grafana dashboard shows job health and alerts**.

### **Frontend Features**
- View all Jenkins jobs, status, duration, anomaly, and insights.
- Click "DETAILS" for job history and LLM-generated explanations.
- See real-time updates as jobs run and complete.

---

## 🔬 Technical Details

### **Backend (Spring Boot)**
- Fetches Jenkins jobs via REST API.
- Calls ML service for anomaly detection (flags any failure).
- Calls Groq LLM for human-readable insights (real API ready).
- Exposes Prometheus metrics for every job and anomaly.
- Logs every action for traceability.

### **ML Service (Python)**
- Receives durations and statuses.
- Flags any job with status FAILURE as anomaly.
- Performs statistical anomaly detection for durations.
- Logs every request and response.

### **LLM (Groq)**
- Receives job context and anomaly/failure details.
- Returns root cause and recommendations.
- Logs prompt and response.

### **Prometheus & Grafana**
- Prometheus scrapes `/actuator/prometheus` for metrics.
- Grafana visualizes job status, duration, and anomaly trends.
- Alertmanager sends Slack alerts for every anomaly/failure.

### **Slack Alerts**
- Every anomaly or failure triggers a Slack alert.
- Alert includes job name, status, and LLM insight.

---

## 📊 Monitoring, Alerting & Observability

- **Prometheus**: Scrapes and stores all job/anomaly metrics.
- **Grafana**: Visualizes job health, trends, and alerts.
- **Alertmanager**: Sends Slack alerts for every anomaly/failure.
- **Logging**: Every service logs all actions (job fetch, ML call, anomaly, LLM, metrics).
- **All logs visible in Docker Compose logs**.

---

## 🤝 Contributing

We welcome contributions! Please:
- Fork the repo and create a feature branch.
- Follow code style and add tests for new features.
- Update documentation as needed.
- Open a pull request with a detailed description.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Jenkins, Prometheus, Grafana, Slack, Groq, OpenAI, Spring Boot, React, Python, Flask, scikit-learn

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/devops-intelligence-dashboard/issues)
- **Email**: support@yourdomain.com

---

<div align="center">

**Made with ❤️ by the DevOps Intelligence Team**

[![GitHub stars](https://img.shields.io/github/stars/yourusername/devops-intelligence-dashboard?style=social)](https://github.com/yourusername/devops-intelligence-dashboard)
[![GitHub forks](https://img.shields.io/github/forks/yourusername/devops-intelligence-dashboard?style=social)](https://github.com/yourusername/devops-intelligence-dashboard)
[![GitHub issues](https://img.shields.io/github/issues/yourusername/devops-intelligence-dashboard)](https://github.com/yourusername/devops-intelligence-dashboard/issues)

</div>

---

**This README is ready for your project! If you want to add a real architecture image, more usage examples, or your actual contact/team info, just let me know!** 
