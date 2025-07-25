package com.example.crudspring.services;

import com.example.crudspring.models.JenkinsJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Service
public class JenkinsService {
    @Value("${jenkins.url}")
    private String defaultJenkinsUrl;

    @Value("${jenkins.job}")
    private String defaultJobName;

    @Value("${jenkins.user}")
    private String defaultJenkinsUser;

    @Value("${jenkins.token}")
    private String defaultJenkinsToken;

    // Dynamic configuration - can be updated via API
    private String jenkinsUrl;
    private String jobName;
    private String jenkinsUser;
    private String jenkinsToken;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final MeterRegistry meterRegistry;

    public JenkinsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        // Initialize with default values
        initializeDefaults();
    }

    private void initializeDefaults() {
        this.jenkinsUrl = this.defaultJenkinsUrl;
        this.jobName = this.defaultJobName;
        this.jenkinsUser = this.defaultJenkinsUser;
        this.jenkinsToken = this.defaultJenkinsToken;
    }

    public Map<String, Object> updateJenkinsConfig(Map<String, String> config) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (config.containsKey("url")) {
                this.jenkinsUrl = config.get("url");
            }
            if (config.containsKey("user")) {
                this.jenkinsUser = config.get("user");
            }
            if (config.containsKey("token")) {
                this.jenkinsToken = config.get("token");
            }
            if (config.containsKey("job")) {
                this.jobName = config.get("job");
            }

            // Test the connection with new credentials
            List<JenkinsJob> jobs = getAllJobs();
            
            result.put("status", "success");
            result.put("message", "Jenkins configuration updated successfully");
            result.put("jobCount", jobs.size());
            result.put("jobs", jobs);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to update Jenkins configuration: " + e.getMessage());
            result.put("jobCount", 0);
        }
        return result;
    }

    public Map<String, Object> getJenkinsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("url", this.jenkinsUrl != null ? this.jenkinsUrl : this.defaultJenkinsUrl);
        config.put("user", this.jenkinsUser != null ? this.jenkinsUser : this.defaultJenkinsUser);
        config.put("job", this.jobName != null ? this.jobName : this.defaultJobName);
        
        // Handle token display safely
        String tokenToShow = this.jenkinsToken != null ? this.jenkinsToken : this.defaultJenkinsToken;
        if (tokenToShow != null && tokenToShow.length() > 4 && !tokenToShow.equals("your-jenkins-token-here")) {
            config.put("token", "***" + tokenToShow.substring(tokenToShow.length() - 4));
        } else {
            config.put("token", "");
        }
        
        return config;
    }

    public void pollJenkinsJob() {
        try {
            System.out.println("[BACKEND] Polling Jenkins job at: " + jenkinsUrl);
            // Call Jenkins API for last build info with Basic Auth
            String apiUrl = jenkinsUrl + "/job/" + jobName + "/lastBuild/api/json";
            HttpHeaders headers = new HttpHeaders();
            String auth = jenkinsUser + ":" + jenkinsToken;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("[BACKEND] Making request to: " + apiUrl);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> buildInfo = (Map<String, Object>) response.getBody();
            if (buildInfo != null) {
                String status = (String) buildInfo.get("result"); // e.g., "SUCCESS"
                Long duration = ((Number) buildInfo.get("duration")).longValue() / 1000; // ms to s
                Long timestampMs = ((Number) buildInfo.get("timestamp")).longValue();
                LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMs), ZoneId.systemDefault());

                // After saving, get all durations for this job
                List<JenkinsJob> jobs = getAllJobs();
                List<Long> durations = jobs.stream().map(JenkinsJob::getDuration).collect(Collectors.toList());

                // Call ML microservice
                Map<String, Object> request = new HashMap<>();
                request.put("durations", durations);
                HttpHeaders mlHeaders = new HttpHeaders();
                mlHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> mlEntity = new HttpEntity<>(request, mlHeaders);
                boolean anomalyDetected = false;
                try {
                    System.out.println("[BACKEND] Calling ML service at: " + mlServiceUrl);
                    ResponseEntity<Map> mlResponse = restTemplate.postForEntity(mlServiceUrl, mlEntity, Map.class);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> mlBody = (Map<String, Object>) mlResponse.getBody();
                    Object anomalies = mlBody.get("anomalies");
                    System.out.println("[BACKEND] ML service response: " + anomalies);
                    if (anomalies instanceof List && !((List<?>) anomalies).isEmpty()) {
                        anomalyDetected = true;
                        System.out.println("[BACKEND] Anomaly detected by ML service!");
                    }
                } catch (Exception e) {
                    System.out.println("[BACKEND] ML service call failed: " + e.getMessage());
                }

                recordJobMetrics(jobName, status, duration, anomalyDetected);
            }
        } catch (Exception e) {
            System.out.println("[BACKEND] Jenkins API call failed: " + e.getMessage());
        }
    }

    public List<JenkinsJob> getAllJobs() {
        try {
            System.out.println("[JENKINS] Fetching jobs from: " + jenkinsUrl);
            System.out.println("[JENKINS] Using user: " + jenkinsUser);
            System.out.println("[JENKINS] Token length: " + (jenkinsToken != null ? jenkinsToken.length() : 0));
            
            // Fetch jobs from Jenkins API
            String apiUrl = jenkinsUrl + "/api/json?tree=jobs[name,color]";
            HttpHeaders headers = new HttpHeaders();
            String auth = jenkinsUser + ":" + jenkinsToken;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("[JENKINS] Making request to: " + apiUrl);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
            System.out.println("[JENKINS] Response status: " + response.getStatusCode());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> jobs = (List<Map<String, Object>>) body.get("jobs");
            
            System.out.println("[JENKINS] Found " + (jobs != null ? jobs.size() : 0) + " jobs");
            
            List<JenkinsJob> result = new java.util.ArrayList<>();
            if (jobs != null) {
                for (Map<String, Object> job : jobs) {
                    String name = (String) job.get("name");
                    System.out.println("[JENKINS] Processing job: " + name);
                    
                    try {
                        // Fetch last build for each job
                        String buildUrl = jenkinsUrl + "/job/" + name + "/lastBuild/api/json";
                        System.out.println("[JENKINS] Fetching build info from: " + buildUrl);
                        ResponseEntity<Map> buildResp = restTemplate.exchange(buildUrl, HttpMethod.GET, entity, Map.class);
                        @SuppressWarnings("unchecked")
                        Map<String, Object> buildInfo = (Map<String, Object>) buildResp.getBody();
                        if (buildInfo != null) {
                            String status = (String) buildInfo.get("result");
                            Long duration = ((Number) buildInfo.get("duration")).longValue() / 1000; // ms to s
                            duration = duration * 3; // Simulate longer duration
                            Long timestampMs = ((Number) buildInfo.get("timestamp")).longValue();
                            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMs), ZoneId.systemDefault());
                            result.add(new JenkinsJob(name, status, timestamp, duration));
                            System.out.println("[JENKINS] Added job: " + name + " with status: " + status);
                        }
                    } catch (Exception buildEx) {
                        System.out.println("[JENKINS] Failed to fetch build info for job " + name + ": " + buildEx.getMessage());
                        // Add job with default values if build info fails
                        result.add(new JenkinsJob(name, "UNKNOWN", LocalDateTime.now(), 0L));
                    }
                }
            }
            System.out.println("[JENKINS] Returning " + result.size() + " jobs");
            return result;
        } catch (Exception e) {
            System.out.println("[JENKINS] Jenkins API call failed: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public void recordJobMetrics(String jobName, String status, long durationSeconds, boolean anomalyDetected) {
        int statusValue = "SUCCESS".equalsIgnoreCase(status) ? 1 : 0;
        meterRegistry.gauge("jenkins_job_status", Tags.of("job", jobName), statusValue);
        meterRegistry.gauge("jenkins_job_duration_seconds", Tags.of("job", jobName), durationSeconds);
        meterRegistry.gauge("jenkins_job_anomaly", Tags.of("job", jobName), anomalyDetected ? 1 : 0);
    }
    
    // Method to manually trigger polling for frontend
    public Map<String, Object> triggerPoll() {
        Map<String, Object> result = new HashMap<>();
        try {
            pollJenkinsJob();
            List<JenkinsJob> jobs = getAllJobs();
            result.put("status", "success");
            result.put("message", "Jenkins polling completed successfully");
            result.put("jobs", jobs);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Jenkins polling failed: " + e.getMessage());
            result.put("jobs", java.util.Collections.emptyList());
        }
        return result;
    }

    public Map<String, Object> debugConnection() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("jenkinsUrl", jenkinsUrl);
            result.put("jenkinsUser", jenkinsUser);
            result.put("tokenLength", jenkinsToken != null ? jenkinsToken.length() : 0);
            
            // Test basic connection
            String apiUrl = jenkinsUrl + "/api/json";
            HttpHeaders headers = new HttpHeaders();
            String auth = jenkinsUser + ":" + jenkinsToken;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("[DEBUG] Testing connection to: " + apiUrl);
            System.out.println("[DEBUG] Auth header: " + authHeader.substring(0, 20) + "...");
            
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
            result.put("connectionStatus", "SUCCESS");
            result.put("responseCode", response.getStatusCode().value());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> jobs = (List<Map<String, Object>>) body.get("jobs");
            result.put("rawJobsCount", jobs != null ? jobs.size() : 0);
            result.put("rawJobs", jobs);
            
        } catch (Exception e) {
            result.put("connectionStatus", "FAILED");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object> getJobInsights(String jobName) {
        System.out.println("[JENKINS] Fetching job insights for: " + jobName);
        List<JenkinsJob> jobs = getAllJobs().stream()
            .filter(j -> j.getJobName().equals(jobName))
            .collect(Collectors.toList());
        System.out.println("[JENKINS] Job history: " + jobs);
        Map<String, Object> result = new HashMap<>();
        result.put("history", jobs);
        boolean anomaly = false;
        String insight = "Normal";
        if (!jobs.isEmpty()) {
            JenkinsJob latest = jobs.get(jobs.size() - 1);
            System.out.println("[JENKINS] Latest job: " + latest);
            // Always call ML service for every job
            try {
                Map<String, Object> request = new HashMap<>();
                request.put("durations", jobs.stream().map(JenkinsJob::getDuration).collect(Collectors.toList()));
                request.put("statuses", jobs.stream().map(JenkinsJob::getStatus).collect(Collectors.toList()));
                request.put("latest_status", latest.getStatus());
                request.put("latest_duration", latest.getDuration());
                HttpHeaders mlHeaders = new HttpHeaders();
                mlHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> mlEntity = new HttpEntity<>(request, mlHeaders);
                System.out.println("[ML] Sending to ML service: " + request);
                ResponseEntity<Map> mlResponse = restTemplate.postForEntity(mlServiceUrl, mlEntity, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> mlBody = (Map<String, Object>) mlResponse.getBody();
                Object anomalies = mlBody.get("anomalies");
                System.out.println("[ML] ML service response: " + anomalies);
                if (anomalies instanceof List && !((List<?>) anomalies).isEmpty()) {
                    anomaly = true;
                    System.out.println("[ANOMALY] ML detected anomaly in job.");
                    insight = callGroqLLMForInsight(latest, true);
                }
            } catch (Exception e) {
                System.out.println("ML service call failed: " + e.getMessage());
            }
            // If job failed (intentional or real), always mark as anomaly
            if ("FAILURE".equalsIgnoreCase(latest.getStatus())) {
                anomaly = true;
                System.out.println("[ANOMALY] Job failed. Marked as anomaly.");
                insight = callGroqLLMForInsight(latest, true);
            }
        }
        result.put("anomaly", anomaly);
        result.put("insight", insight);
        System.out.println("[RESULT] Anomaly: " + anomaly + ", Insight: " + insight);
        return result;
    }

    // Real Groq LLM API call (with API key placeholder)
    private String callGroqLLMForInsight(JenkinsJob job, boolean isFailure) {
        String apiKey = "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"; // <--- PLACEHOLDER
        String prompt;
        if (isFailure) {
            prompt = "Job '" + job.getJobName() + "' failed (duration: " + job.getDuration() + "). Possible root causes: misconfiguration, missing build steps, code error, or infrastructure issue. Please check Jenkins logs.";
        } else {
            prompt = "Anomaly detected in job '" + job.getJobName() + "' with duration " + job.getDuration() + ". Possible causes: performance bottleneck, resource contention, or external dependency issues.";
        }
        System.out.println("[LLM] (Groq) Prompt: " + prompt);
        // TODO: Replace with real Groq API call using apiKey
        // Simulate LLM response
        String llmResponse = "Groq LLM Insight: " + prompt;
        System.out.println("[LLM] (Groq) Response: " + llmResponse);
        return llmResponse;
    }
} 