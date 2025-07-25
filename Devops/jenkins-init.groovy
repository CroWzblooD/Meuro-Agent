import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
import hudson.model.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

def instance = Jenkins.getInstance()

// Create admin user
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin")
instance.setSecurityRealm(hudsonRealm)

// Set authorization strategy
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

// Create fail-job pipeline
def jobName = "fail-job"
def job = instance.getItem(jobName)
if (job == null) {
    job = instance.createProject(WorkflowJob.class, jobName)
    
    // Simple pipeline script that fails occasionally for testing
    def pipelineScript = '''
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                script {
                    // Randomly fail 30% of the time for testing anomaly detection
                    def random = new Random()
                    if (random.nextInt(10) < 3) {
                        error("Simulated failure for testing")
                    }
                    echo "Build successful"
                    sleep(time: random.nextInt(30) + 10, unit: 'SECONDS')
                }
            }
        }
    }
}
'''
    
    job.setDefinition(new CpsFlowDefinition(pipelineScript, true))
    job.save()
}

instance.save()
println "Jenkins setup completed!"