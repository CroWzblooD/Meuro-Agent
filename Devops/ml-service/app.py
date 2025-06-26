from flask import Flask, request, jsonify
from sklearn.ensemble import IsolationForest, RandomForestClassifier
import numpy as np
import pandas as pd
import nltk
from nltk.tokenize import word_tokenize
from collections import Counter
import re

app = Flask(__name__)

@app.route('/analyze', methods=['POST'])
def analyze():
    data = request.get_json()
    durations = np.array(data.get('durations', []))
    statuses = data.get('statuses', [])
    latest_status = data.get('latest_status', '')
    latest_duration = data.get('latest_duration', 0)
    print(f"[ML] Received durations: {durations}, statuses: {statuses}, latest_status: {latest_status}, latest_duration: {latest_duration}")
    anomalies = []
    # Flag any FAILURE as anomaly
    if latest_status == 'FAILURE':
        anomalies.append(int(latest_duration))
        print(f"[ML] Detected anomaly due to FAILURE status: {latest_duration}")
    # Also do statistical anomaly detection
    if len(durations) > 2:
        mean = durations.mean()
        std = durations.std()
        for i, d in enumerate(durations):
            if abs(d - mean) > 2 * std:
                anomalies.append(int(d))
        print(f"[ML] Detected statistical anomalies: {anomalies}")
    response = {'anomalies': anomalies}
    print(f"[ML] Response: {response}")
    return jsonify(response)

@app.route('/predict-failure', methods=['POST'])
def predict_failure():
    data = request.json
    # Expecting a list of dicts: [{"duration": 120, "status": 1}, ...] where status 1=SUCCESS, 0=FAILURE
    history = data.get('history', [])
    if len(history) < 5:
        return jsonify({'prob_failure': 0.0, 'note': 'Not enough data'})
    df = pd.DataFrame(history)
    X = df[['duration']]
    y = df['status']
    clf = RandomForestClassifier(n_estimators=10, random_state=42)
    clf.fit(X, y)
    # Predict next build as failure if duration is above mean
    next_duration = np.mean(df['duration'])
    prob_failure = 1 - clf.predict_proba([[next_duration]])[0][1]
    return jsonify({'prob_failure': float(prob_failure)})

@app.route('/analyze-logs', methods=['POST'])
def analyze_logs():
    data = request.json
    logs = data.get('logs', '')
    if not logs:
        return jsonify({'root_causes': []})
    # Simple NLP: extract error lines and most common error keywords
    error_lines = [line for line in logs.split('\n') if 'error' in line.lower() or 'exception' in line.lower()]
    tokens = []
    for line in error_lines:
        tokens += word_tokenize(re.sub(r'[^a-zA-Z0-9 ]', '', line.lower()))
    common = Counter(tokens).most_common(5)
    root_causes = [word for word, count in common if word not in ('error', 'exception', 'the', 'a', 'an', 'to', 'in', 'of', 'and')]
    return jsonify({'root_causes': root_causes, 'error_lines': error_lines})

if __name__ == '__main__':
    nltk.download('punkt')
    app.run(host='0.0.0.0', port=5000) 