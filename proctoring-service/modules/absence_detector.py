"""
Absence Detection Module
Tracks when student's face is not visible for extended periods
"""

from datetime import datetime, timedelta

class AbsenceDetector:
    def __init__(self):
        """Initialize absence detector"""
        self.absence_records = {}  # {attempt_id: {'count': int, 'last_seen': datetime}}
        self.threshold_count = 3  # Alert after 3 consecutive absences
        self.reset_timeout = 60  # Reset counter after 60 seconds of presence
    
    def record_absence(self, attempt_id):
        """
        Record an absence event
        
        Args:
            attempt_id: Exam attempt ID
        """
        if attempt_id not in self.absence_records:
            self.absence_records[attempt_id] = {
                'count': 0,
                'last_seen': None
            }
        
        self.absence_records[attempt_id]['count'] += 1
        self.absence_records[attempt_id]['last_seen'] = datetime.now()
    
    def reset_absence(self, attempt_id):
        """
        Reset absence counter when face is detected
        
        Args:
            attempt_id: Exam attempt ID
        """
        if attempt_id in self.absence_records:
            # Check if enough time has passed to reset
            last_seen = self.absence_records[attempt_id]['last_seen']
            if last_seen:
                time_diff = (datetime.now() - last_seen).total_seconds()
                if time_diff > self.reset_timeout:
                    self.absence_records[attempt_id]['count'] = 0
            else:
                self.absence_records[attempt_id]['count'] = 0
    
    def is_suspicious(self, attempt_id):
        """
        Check if absence is suspicious
        
        Args:
            attempt_id: Exam attempt ID
        
        Returns:
            bool: True if suspicious
        """
        if attempt_id not in self.absence_records:
            return False
        
        count = self.absence_records[attempt_id]['count']
        return count >= self.threshold_count
    
    def get_absence_count(self, attempt_id):
        """
        Get absence count for an attempt
        
        Args:
            attempt_id: Exam attempt ID
        
        Returns:
            int: Absence count
        """
        if attempt_id not in self.absence_records:
            return 0
        
        return self.absence_records[attempt_id]['count']
    
    def get_severity(self, attempt_id):
        """
        Get severity level based on absence count
        
        Args:
            attempt_id: Exam attempt ID
        
        Returns:
            str: Severity level (LOW, MEDIUM, HIGH)
        """
        count = self.get_absence_count(attempt_id)
        
        if count == 0:
            return 'LOW'
        elif count < self.threshold_count:
            return 'MEDIUM'
        else:
            return 'HIGH'
