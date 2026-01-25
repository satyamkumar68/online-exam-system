"""
Proctoring Logger Module
Logs all proctoring events
"""

from datetime import datetime
import json
import os

class ProctoringLogger:
    def __init__(self):
        """Initialize logger"""
        self.logs = {}  # {attempt_id: [events]}
        self.log_file = 'proctoring_logs.json'
    
    def log_event(self, attempt_id, event_type, description, severity='LOW'):
        """
        Log a proctoring event
        
        Args:
            attempt_id: Exam attempt ID
            event_type: Type of event (FACE_DETECTED, NO_FACE, MULTIPLE_FACES, etc.)
            description: Event description
            severity: Severity level (LOW, MEDIUM, HIGH)
        """
        if attempt_id not in self.logs:
            self.logs[attempt_id] = []
        
        event = {
            'attemptId': attempt_id,
            'eventType': event_type,
            'description': description,
            'severity': severity,
            'timestamp': datetime.now().isoformat()
        }
        
        self.logs[attempt_id].append(event)
        
        # Save to file
        self.save_to_file()
        
        # Print to console
        print(f"[{severity}] Attempt {attempt_id}: {event_type} - {description}")
    
    def get_logs(self, attempt_id):
        """
        Get all logs for an attempt
        
        Args:
            attempt_id: Exam attempt ID
        
        Returns:
            List of log events
        """
        return self.logs.get(attempt_id, [])
    
    def get_high_severity_logs(self, attempt_id):
        """
        Get high severity logs for an attempt
        
        Args:
            attempt_id: Exam attempt ID
        
        Returns:
            List of high severity events
        """
        all_logs = self.get_logs(attempt_id)
        return [log for log in all_logs if log['severity'] == 'HIGH']
    
    def get_suspicious_activity_count(self, attempt_id):
        """
        Count suspicious activities
        
        Args:
            attempt_id: Exam attempt ID
        
        Returns:
            int: Count of high severity events
        """
        return len(self.get_high_severity_logs(attempt_id))
    
    def save_to_file(self):
        """Save logs to JSON file"""
        try:
            with open(self.log_file, 'w') as f:
                json.dump(self.logs, f, indent=2)
        except Exception as e:
            print(f"Error saving logs: {str(e)}")
    
    def load_from_file(self):
        """Load logs from JSON file"""
        try:
            if os.path.exists(self.log_file):
                with open(self.log_file, 'r') as f:
                    self.logs = json.load(f)
        except Exception as e:
            print(f"Error loading logs: {str(e)}")
    
    def clear_logs(self, attempt_id=None):
        """
        Clear logs
        
        Args:
            attempt_id: If provided, clear only for this attempt. Otherwise clear all.
        """
        if attempt_id:
            if attempt_id in self.logs:
                del self.logs[attempt_id]
        else:
            self.logs = {}
        
        self.save_to_file()
    
    def generate_report(self, attempt_id):
        """
        Generate proctoring report for an attempt
        
        Args:
            attempt_id: Exam attempt ID
        
        Returns:
            dict: Report summary
        """
        logs = self.get_logs(attempt_id)
        
        if not logs:
            return {
                'attemptId': attempt_id,
                'totalEvents': 0,
                'suspiciousActivities': 0,
                'status': 'NO_DATA'
            }
        
        total_events = len(logs)
        high_severity = len([l for l in logs if l['severity'] == 'HIGH'])
        medium_severity = len([l for l in logs if l['severity'] == 'MEDIUM'])
        low_severity = len([l for l in logs if l['severity'] == 'LOW'])
        
        # Determine overall status
        if high_severity > 5:
            status = 'HIGHLY_SUSPICIOUS'
        elif high_severity > 0:
            status = 'SUSPICIOUS'
        elif medium_severity > 10:
            status = 'NEEDS_REVIEW'
        else:
            status = 'NORMAL'
        
        return {
            'attemptId': attempt_id,
            'totalEvents': total_events,
            'suspiciousActivities': high_severity,
            'mediumSeverity': medium_severity,
            'lowSeverity': low_severity,
            'status': status,
            'recommendation': self.get_recommendation(status)
        }
    
    def get_recommendation(self, status):
        """Get recommendation based on status"""
        recommendations = {
            'HIGHLY_SUSPICIOUS': 'Recommend manual review and possible exam invalidation',
            'SUSPICIOUS': 'Recommend manual review of exam attempt',
            'NEEDS_REVIEW': 'Minor issues detected, review recommended',
            'NORMAL': 'No significant issues detected',
            'NO_DATA': 'No proctoring data available'
        }
        return recommendations.get(status, 'Unknown status')
