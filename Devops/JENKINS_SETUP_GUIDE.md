# 🚀 Jenkins Setup Guide for DevOps Dashboard

## 📋 Prerequisites
- Jenkins running on http://localhost:8080
- User: `Crowblood`
- API Token: `11f7af8b164e1838c4221affccf7161ee2`

## 🔧 Step 1: Create the `fail-job` Pipeline

1. **Open Jenkins** → http://localhost:8080
2. **Login** with your credentials
3. **Click "New Item"**
4. **Enter name**: `fail-job`
5. **Select**: "Pipeline"
6. **Click "OK"**

## 📝 Step 2: Add Pipeline Script

In the Pipeline section, add this script:

```groovy
pipeline {
    agent any
    stages {
        stage('Test Stage') {
            steps {
                script {
                    // Randomly fail 30% of the time for testing anomaly detection
                    def random = new Random()
                    def shouldFail = random.nextInt(10) < 3
                    
                    if (shouldFail) {
                        echo "❌ Simulating failure for testing..."
                        error("Simulated failure for anomaly detection testing")
                    } else {
                        echo "✅ Build successful!"
                        // Random duration between 10-40 seconds
                        def duration = random.nextInt(30) + 10
                        sleep(time: duration, unit: 'SECONDS')
                        echo "Completed after ${duration} seconds"
                    }
                }
            }
        }
    }
}
```

7. **Click "Save"**

## 🔑 Step 3: Update API Token (if needed)

If you need to generate a new API token:

1. **Click your username** (top right) → **Configure**
2. **API Token section** → **Add new Token**
3. **Name**: `SpringBoot-Dashboard`
4. **Generate** → **Copy the token**
5. **Update** `application.properties`:
   ```properties
   jenkins.token=YOUR_NEW_TOKEN_HERE
   ```

## 🎯 Step 4: Test the Setup

1. **Run the job manually** in Jenkins to create some build history
2. **Start your Docker services**:
   ```cmd
   docker-compose up --build -d
   ```
3. **Open the dashboard**: http://localhost:3000
4. **Click "Test Connection"** - should show ✅ success
5. **Click "Poll Jenkins Job"** - should fetch your job data

## 🔍 What You'll See in the Dashboard

- **Job Name**: `fail-job`
- **Status**: SUCCESS/FAILURE (color-coded)
- **Duration**: Build time in seconds
- **Timestamp**: When the build ran
- **Details**: Click to see ML insights and anomaly detection

## 🚨 Troubleshooting

### 401 Unauthorized Error:
- Check username/token in `application.properties`
- Verify Jenkins is running on port 8080
- Generate new API token if needed

### No Jobs Found:
- Make sure `fail-job` exists in Jenkins
- Run the job at least once to create build history
- Check Jenkins job name matches exactly

### Connection Failed:
- Verify Jenkins is accessible at http://localhost:8080
- Check Docker containers are running: `docker-compose ps`
- Check Spring Boot logs: `docker-compose logs -f springboot-backend`

## 🎉 Success Indicators

✅ **Test Connection** shows job count > 0  
✅ **Poll Jenkins Job** displays job data in table  
✅ **Details** button shows insights and history  
✅ **Failed jobs** are highlighted in red  
✅ **ML anomaly detection** provides insights  

## 📊 Dashboard Features

- **Real-time polling** of Jenkins jobs
- **Anomaly detection** using ML service
- **Job history** and duration tracking
- **Visual status indicators** (success/failure)
- **AI-powered insights** for failed builds
- **Responsive design** for all devices