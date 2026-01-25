"""
Multiple Face Detection Module
Detects when more than one person is present
"""

from modules.face_detector import FaceDetector

class MultiFaceDetector:
    def __init__(self):
        """Initialize multi-face detector"""
        self.face_detector = FaceDetector()
        self.threshold = 1  # Alert if more than 1 face
    
    def check_multiple_faces(self, image):
        """
        Check if multiple faces are present
        
        Args:
            image: OpenCV image
        
        Returns:
            dict: {
                'hasMultipleFaces': bool,
                'faceCount': int,
                'severity': str
            }
        """
        faces = self.face_detector.detect_faces(image)
        face_count = len(faces)
        
        has_multiple = face_count > self.threshold
        
        # Determine severity
        if face_count == 0:
            severity = 'MEDIUM'
        elif face_count == 1:
            severity = 'LOW'
        elif face_count == 2:
            severity = 'HIGH'
        else:
            severity = 'HIGH'
        
        return {
            'hasMultipleFaces': has_multiple,
            'faceCount': face_count,
            'severity': severity
        }
    
    def get_alert_message(self, face_count):
        """
        Get alert message based on face count
        
        Args:
            face_count: Number of faces detected
        
        Returns:
            Alert message string
        """
        if face_count == 0:
            return "No face detected"
        elif face_count == 1:
            return "Single face detected (normal)"
        elif face_count == 2:
            return "Warning: 2 faces detected"
        else:
            return f"Critical: {face_count} faces detected"
