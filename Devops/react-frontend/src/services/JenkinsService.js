import axios from 'axios';

const API_BASE_URL = 'http://localhost:8020/jenkins';

class JenkinsService {
    getJobs() {
        return axios.get(`${API_BASE_URL}/jobs`);
    }
    pollJob() {
        return axios.post(`${API_BASE_URL}/poll`);
    }
    getJobDetails(jobName) {
        return axios.get(`${API_BASE_URL}/jobs/${jobName}`);
    }
}

export default new JenkinsService(); 