import React, { Component } from 'react';
import JenkinsService from '../services/JenkinsService';

class JenkinsDashboardComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            jobs: [],
            loading: false,
            error: null,
            selectedJob: null,
            jobDetails: null,
            showDetails: false,
            showConfig: false,
            jenkinsConfig: {
                url: 'http://localhost:8080',
                user: '',
                token: '',
                job: 'test-job'
            },
            configStatus: null
        };
        this.handlePoll = this.handlePoll.bind(this);
        this.fetchJobDetails = this.fetchJobDetails.bind(this);
        this.testConnection = this.testConnection.bind(this);
        this.updateJenkinsConfig = this.updateJenkinsConfig.bind(this);
        this.handleConfigChange = this.handleConfigChange.bind(this);
        this.loadJenkinsConfig = this.loadJenkinsConfig.bind(this);
    }

    componentDidMount() {
        this.loadJenkinsConfig();
        this.fetchJobs();
    }

    loadJenkinsConfig() {
        JenkinsService.getJenkinsConfig()
            .then(res => {
                this.setState({ 
                    jenkinsConfig: {
                        ...this.state.jenkinsConfig,
                        ...res.data
                    }
                });
            })
            .catch(err => {
                console.log('Could not load Jenkins config:', err.message);
            });
    }

    handleConfigChange(field, value) {
        this.setState({
            jenkinsConfig: {
                ...this.state.jenkinsConfig,
                [field]: value
            }
        });
    }

    updateJenkinsConfig() {
        this.setState({ loading: true, configStatus: null });
        JenkinsService.updateJenkinsConfig(this.state.jenkinsConfig)
            .then(res => {
                if (res.data.status === 'success') {
                    this.setState({ 
                        jobs: res.data.jobs || [], 
                        loading: false,
                        configStatus: 'success',
                        showConfig: false,
                        error: null
                    });
                    alert(`✅ Jenkins Configuration Updated!\nFound ${res.data.jobCount} jobs`);
                } else {
                    this.setState({ 
                        configStatus: 'error',
                        error: res.data.message || 'Configuration update failed', 
                        loading: false 
                    });
                }
            })
            .catch(err => {
                this.setState({ 
                    configStatus: 'error',
                    error: 'Configuration update failed: ' + err.message, 
                    loading: false 
                });
            });
    }

    fetchJobs() {
        this.setState({ loading: true });
        JenkinsService.getJobs()
            .then(res => {
                this.setState({ jobs: res.data, loading: false });
            })
            .catch(err => {
                this.setState({ error: err.message, loading: false });
            });
    }

    handlePoll() {
        this.setState({ loading: true, error: null });
        JenkinsService.pollJob()
            .then(res => {
                if (res.data.status === 'success') {
                    this.setState({ 
                        jobs: res.data.jobs || [], 
                        loading: false,
                        error: null 
                    });
                } else {
                    this.setState({ 
                        error: res.data.message || 'Polling failed', 
                        loading: false 
                    });
                }
            })
            .catch(err => {
                this.setState({ 
                    error: 'Failed to connect to Jenkins: ' + err.message, 
                    loading: false 
                });
            });
    }

    fetchJobDetails(jobName) {
        JenkinsService.getJobDetails(jobName)
            .then(res => {
                this.setState({ jobDetails: res.data, showDetails: true });
            })
            .catch(err => {
                this.setState({ error: err.message });
            });
    }

    testConnection() {
        this.setState({ loading: true, error: null });
        JenkinsService.testConnection()
            .then(res => {
                if (res.data.status === 'success') {
                    this.setState({ 
                        jobs: res.data.jobs || [], 
                        loading: false,
                        error: null 
                    });
                    alert(`✅ Jenkins Connection Successful!\nFound ${res.data.jobCount} jobs`);
                } else {
                    this.setState({ 
                        error: res.data.message || 'Connection test failed', 
                        loading: false 
                    });
                }
            })
            .catch(err => {
                this.setState({ 
                    error: 'Connection test failed: ' + err.message, 
                    loading: false 
                });
            });
    }

    render() {
        const { jobs, loading, error, jobDetails, showDetails, showConfig, jenkinsConfig } = this.state;
        return (
            <div className="container mt-4">
                <div className="card shadow mb-4">
                    <div className="card-body">
                        <h2 className="card-title text-center mb-4">DevOps Intelligence Dashboard</h2>
                        <p className="text-muted text-center mb-4">
                            <b>Quick Setup:</b> Configure your Jenkins credentials below, then start monitoring your jobs!<br/>
                            Create Jenkins jobs at <code>http://localhost:8080</code> and watch them here in real-time.
                        </p>
                        
                        {/* Jenkins Configuration Panel */}
                        <div className="text-center mb-3">
                            <button 
                                className="btn btn-warning me-2" 
                                onClick={() => this.setState({ showConfig: !showConfig })}
                                disabled={loading}
                            >
                                ⚙️ Configure Jenkins
                            </button>
                            <button className="btn btn-primary me-2" onClick={this.handlePoll} disabled={loading}>
                                {loading ? 'Polling...' : '🔄 Refresh Jobs'}
                            </button>
                            <button className="btn btn-outline-secondary" onClick={this.testConnection} disabled={loading}>
                                🔗 Test Connection
                            </button>
                        </div>

                        {/* Configuration Form */}
                        {showConfig && (
                            <div className="card mb-4" style={{backgroundColor: '#f8f9fa'}}>
                                <div className="card-body">
                                    <h5 className="card-title">Jenkins Configuration</h5>
                                    <div className="row">
                                        <div className="col-md-6">
                                            <div className="mb-3">
                                                <label className="form-label">Jenkins URL</label>
                                                <input 
                                                    type="text" 
                                                    className="form-control" 
                                                    value={jenkinsConfig.url}
                                                    onChange={(e) => this.handleConfigChange('url', e.target.value)}
                                                    placeholder="http://localhost:8080"
                                                />
                                            </div>
                                            <div className="mb-3">
                                                <label className="form-label">Username</label>
                                                <input 
                                                    type="text" 
                                                    className="form-control" 
                                                    value={jenkinsConfig.user}
                                                    onChange={(e) => this.handleConfigChange('user', e.target.value)}
                                                    placeholder="Your Jenkins username"
                                                />
                                            </div>
                                        </div>
                                        <div className="col-md-6">
                                            <div className="mb-3">
                                                <label className="form-label">API Token</label>
                                                <input 
                                                    type="password" 
                                                    className="form-control" 
                                                    value={jenkinsConfig.token}
                                                    onChange={(e) => this.handleConfigChange('token', e.target.value)}
                                                    placeholder="Your Jenkins API token"
                                                />
                                                <small className="form-text text-muted">
                                                    Get your token from Jenkins → User → Configure → API Token
                                                </small>
                                            </div>
                                            <div className="mb-3">
                                                <label className="form-label">Default Job (Optional)</label>
                                                <input 
                                                    type="text" 
                                                    className="form-control" 
                                                    value={jenkinsConfig.job}
                                                    onChange={(e) => this.handleConfigChange('job', e.target.value)}
                                                    placeholder="test-job"
                                                />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="text-center">
                                        <button 
                                            className="btn btn-success me-2" 
                                            onClick={this.updateJenkinsConfig}
                                            disabled={loading || !jenkinsConfig.user || !jenkinsConfig.token}
                                        >
                                            💾 Save & Test Configuration
                                        </button>
                                        <button 
                                            className="btn btn-secondary" 
                                            onClick={() => this.setState({ showConfig: false })}
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}
                        {error && <div className="alert alert-danger">{error}</div>}
                        <div className="table-responsive">
                            <table className="table table-bordered table-hover">
                                <thead className="thead-dark">
                                    <tr>
                                        <th>Job Name</th>
                                        <th>Status</th>
                                        <th>Timestamp</th>
                                        <th>Duration (s)</th>
                                        <th>Details</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {jobs.length === 0 && (
                                        <tr><td colSpan="5" className="text-center">No job data yet. Click "Poll Jenkins Job".</td></tr>
                                    )}
                                    {jobs.map(job => (
                                        <tr key={job.jobName} className={job.status === 'FAILURE' ? 'table-danger' : job.status === 'SUCCESS' ? 'table-success' : ''}>
                                            <td>{job.jobName}</td>
                                            <td>
                                                <span className={
                                                    job.status === 'SUCCESS' ? 'badge badge-success' :
                                                    job.status === 'FAILURE' ? 'badge badge-danger' :
                                                    'badge badge-secondary'
                                                }>
                                                    {job.status}
                                                </span>
                                            </td>
                                            <td>{job.timestamp}</td>
                                            <td>{job.duration}</td>
                                            <td>
                                                <button className="btn btn-info btn-sm" onClick={() => this.fetchJobDetails(job.jobName)}>Details</button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        {showDetails && jobDetails && (
                            <div className="mt-4">
                                <h5>Job Insights</h5>
                                <p><b>Anomaly:</b> {jobDetails.anomaly ? 'Yes' : 'No'}</p>
                                <p><b>Insight:</b> {jobDetails.insight}</p>
                                <h6>History</h6>
                                <table className="table table-sm">
                                    <thead>
                                        <tr>
                                            <th>Timestamp</th>
                                            <th>Status</th>
                                            <th>Duration (s)</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {jobDetails.history && jobDetails.history.map((h, i) => (
                                            <tr key={i}>
                                                <td>{h.timestamp}</td>
                                                <td>{h.status}</td>
                                                <td>{h.duration}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                                <button className="btn btn-secondary btn-sm" onClick={() => this.setState({ showDetails: false })}>Close</button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        );
    }
}

export default JenkinsDashboardComponent; 