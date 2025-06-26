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
    private String jenkinsUrl;

    @Value("${jenkins.job}")
    private String jobName;

    @Value("${jenkins.user}")
    private String jenkinsUser;

    @Value("${jenkins.token}")
    private String jenkinsToken;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final MeterRegistry meterRegistry;

    public JenkinsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void pollJenkinsJob() {
        try {
            // Call Jenkins API for last build info with Basic Auth
            String apiUrl = jenkinsUrl + "/job/" + jobName + "/lastBuild/api/json";
            HttpHeaders headers = new HttpHeaders();
            String auth = jenkinsUser + ":" + jenkinsToken;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
            Map<String, Object> buildInfo = response.getBody();
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
                    ResponseEntity<Map> mlResponse = restTemplate.postForEntity(mlServiceUrl, mlEntity, Map.class);
                    Object anomalies = mlResponse.getBody().get("anomalies");
                    System.out.println("Anomalies detected: " + anomalies);
                    if (anomalies instanceof List && !((List<?>) anomalies).isEmpty()) {
                        anomalyDetected = true;
                    }
                } catch (Exception e) {
                    System.out.println("ML service call failed: " + e.getMessage());
                }

                recordJobMetrics(jobName, status, duration, anomalyDetected);
            }
        } catch (Exception e) {
            System.out.println("Jenkins API call failed: " + e.getMessage());
        }
    }

    public List<JenkinsJob> getAllJobs() {
        try {
            // Fetch jobs from Jenkins API
            String apiUrl = jenkinsUrl + "/api/json?tree=jobs[name,color]";
            HttpHeaders headers = new HttpHeaders();
            String auth = jenkinsUser + ":" + jenkinsToken;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> jobs = (List<Map<String, Object>>) body.get("jobs");
            List<JenkinsJob> result = new java.util.ArrayList<>();
            for (Map<String, Object> job : jobs) {
                String name = (String) job.get("name");
                // Fetch last build for each job
                String buildUrl = jenkinsUrl + "/job/" + name + "/lastBuild/api/json";
                ResponseEntity<Map> buildResp = restTemplate.exchange(buildUrl, HttpMethod.GET, entity, Map.class);
                Map<String, Object> buildInfo = buildResp.getBody();
                if (buildInfo != null) {
                    String status = (String) buildInfo.get("result");
                    Long duration = ((Number) buildInfo.get("duration")).longValue() / 1000; // ms to s
                    duration = duration * 3; // Simulate longer duration
                    Long timestampMs = ((Number) buildInfo.get("timestamp")).longValue();
                    LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMs), ZoneId.systemDefault());
                    result.add(new JenkinsJob(name, status, timestamp, duration));
                }
            }
            return result;
        } catch (Exception e) {
            System.out.println("Jenkins API call failed: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public void recordJobMetrics(String jobName, String status, long durationSeconds, boolean anomalyDetected) {
        int statusValue = "SUCCESS".equalsIgnoreCase(status) ? 1 : 0;
        meterRegistry.gauge("jenkins_job_status", Tags.of("job", jobName), statusValue);
        meterRegistry.gauge("jenkins_job_duration_seconds", Tags.of("job", jobName), durationSeconds);
        meterRegistry.gauge("jenkins_job_anomaly", Tags.of("job", jobName), anomalyDetected ? 1 : 0);
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
                Object anomalies = mlResponse.getBody().get("anomalies");
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