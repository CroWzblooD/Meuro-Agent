INSERT INTO jenkins_jobs (job_name, status, timestamp, duration) VALUES
('fail-job', 'SUCCESS', '2025-06-25 10:00:00', 10),
('fail-job', 'SUCCESS', '2025-06-25 10:10:00', 12),
('fail-job', 'SUCCESS', '2025-06-25 10:20:00', 11),
('fail-job', 'SUCCESS', '2025-06-25 10:30:00', 13),
('fail-job', 'SUCCESS', '2025-06-25 10:40:00', 120), -- Anomaly
('fail-job', 'SUCCESS', '2025-06-25 10:50:00', 12),
('fail-job', 'FAILURE', '2025-06-26 12:00:00', 60); 