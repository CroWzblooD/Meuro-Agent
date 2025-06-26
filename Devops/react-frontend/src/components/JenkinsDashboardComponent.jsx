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
            showDetails: false
        };
        this.handlePoll = this.handlePoll.bind(this);
        this.fetchJobDetails = this.fetchJobDetails.bind(this);
    }

    componentDidMount() {
        this.fetchJobs();
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
        JenkinsService.pollJob().then(() => {
            this.fetchJobs();
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

    render() {
        const { jobs, loading, error, jobDetails, showDetails } = this.state;
        return (
            <div className="container mt-4">
                <div className="card shadow mb-4">
                    <div className="card-body">
                        <h2 className="card-title text-center mb-4">DevOps Intelligence Dashboard</h2>
                        <p className="text-muted text-center mb-4">
                            <b>Instructions:</b> Run your Jenkins job at <code>http://localhost:8080</code>.<br/>
                            Click <b>"Poll Jenkins Job"</b> to fetch the latest status and metrics.<br/>
                            Anomalies and failed jobs will be highlighted.
                        </p>
                        <div className="text-center mb-3">
                            <button className="btn btn-primary" onClick={this.handlePoll} disabled={loading}>
                                {loading ? 'Polling...' : 'Poll Jenkins Job'}
                            </button>
                        </div>
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