package com.example.crudspring.models;

import java.time.LocalDateTime;

public class JenkinsJob {
    private String jobName;
    private String status;
    private LocalDateTime timestamp;
    private Long duration;

    public JenkinsJob() {}

    public JenkinsJob(String jobName, String status, LocalDateTime timestamp, Long duration) {
        this.jobName = jobName;
        this.status = status;
        this.timestamp = timestamp;
        this.duration = duration;
    }

    // Getters and setters
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }
} 