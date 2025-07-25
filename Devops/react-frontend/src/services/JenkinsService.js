import axios from 'axios';

const API_BASE_URL = 'http://localhost:8020/api';

class JenkinsService {
    getJobs() {
        return axios.get(`${API_BASE_URL}/jobs`);
    }
    
    pollJob() {
        // This will trigger the polling and return the jobs
        return axios.post(`${API_BASE_URL}/jenkins/poll`);
    }
    
    getJobDetails(jobName) {
        return axios.get(`${API_BASE_URL}/jobs/${jobName}`);
    }
    
    testConnection() {
        return axios.get(`${API_BASE_URL}/jenkins/test`);
    }
    
    updateJenkinsConfig(config) {
        return axios.post(`${API_BASE_URL}/jenkins/config`, config);
    }
    
    getJenkinsConfig() {
        return axios.get(`${API_BASE_URL}/jenkins/config`);
    }
}

export default new JenkinsService(); 