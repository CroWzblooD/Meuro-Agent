package com.example.crudspring.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.crudspring.services.JenkinsService;
import com.example.crudspring.models.JenkinsJob;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class JenkinsController {
    private final JenkinsService jenkinsService;

    public JenkinsController(JenkinsService jenkinsService) {
        this.jenkinsService = jenkinsService;
    }

    @GetMapping("/api/jobs")
    public List<JenkinsJob> getAllJobs() {
        return jenkinsService.getAllJobs();
    }

    @GetMapping("/api/jobs/{jobName}")
    public Map<String, Object> getJobDetails(@PathVariable String jobName) {
        return jenkinsService.getJobInsights(jobName);
    }

    @GetMapping("/api/jenkins/test")
    public Map<String, Object> testJenkinsConnection() {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            List<JenkinsJob> jobs = jenkinsService.getAllJobs();
            result.put("status", "success");
            result.put("message", "Connected to Jenkins successfully");
            result.put("jobCount", jobs.size());
            result.put("jobs", jobs);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to connect to Jenkins: " + e.getMessage());
            result.put("jobCount", 0);
            result.put("error", e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/api/jenkins/debug")
    public Map<String, Object> debugJenkinsConnection() {
        return jenkinsService.debugConnection();
    }

    @PostMapping("/api/jenkins/poll")
    public Map<String, Object> triggerPoll() {
        return jenkinsService.triggerPoll();
    }

    @PostMapping("/api/jenkins/config")
    public Map<String, Object> updateJenkinsConfig(@RequestBody Map<String, String> config) {
        return jenkinsService.updateJenkinsConfig(config);
    }

    @GetMapping("/api/jenkins/config")
    public Map<String, Object> getJenkinsConfig() {
        return jenkinsService.getJenkinsConfig();
    }
} 