package com.example.crudspring.controller;

import com.example.crudspring.models.JenkinsJob;
import com.example.crudspring.services.JenkinsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/jenkins")
public class JenkinsController {
    @Autowired
    private JenkinsService jenkinsService;

    @GetMapping("/jobs")
    public List<JenkinsJob> getAllJobs() {
        return jenkinsService.getAllJobs();
    }

    @PostMapping("/poll")
    public void pollJenkinsJob() {
        jenkinsService.pollJenkinsJob();
    }

    @GetMapping("/jobs/{jobName}")
    public Object getJobDetails(@PathVariable String jobName) {
        return jenkinsService.getJobInsights(jobName);
    }
} 