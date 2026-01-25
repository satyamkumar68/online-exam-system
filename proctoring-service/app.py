"""
AI-Based Proctoring Service
Flask application for real-time exam proctoring using OpenCV
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import cv2
import numpy as np
import base64
from datetime import datetime
import requests
from modules.face_detector import FaceDetector
from modules.multi_face_detector import MultiFaceDetector
from modules.absence_detector import AbsenceDetector
from modules.logger import ProctoringLogger

app = Flask(__name__)
CORS(app)

# Initialize detectors
face_detector = FaceDetector()
multi_face_detector = MultiFaceDetector()
absence_detector = AbsenceDetector()
logger = ProctoringLogger()

# Backend API URL
BACKEND_URL = "http://localhost:8081/api"

import os
from dotenv import load_dotenv
from functools import wraps

load_dotenv()
PROCTORING_SECRET_KEY = os.getenv('PROCTORING_SECRET_KEY')

def require_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get('X-Proctoring-Token')
        if not token or token != PROCTORING_SECRET_KEY:
            return jsonify({'success': False, 'message': 'Unauthorized'}), 401
        return f(*args, **kwargs)
    return decorated

@app.route('/proctoring/start', methods=['POST'])
@require_auth
def start_proctoring():
    """Start a proctoring session"""
    try:
        data = request.json
        attempt_id = data.get('attemptId')
        
        if not attempt_id:
            return jsonify({'success': False, 'message': 'Attempt ID required'}), 400
        
        # Initialize session
        logger.log_event(attempt_id, 'SESSION_START', 'Proctoring session started', 'LOW')
        
        return jsonify({
            'success': True,
            'message': 'Proctoring started',
            'attemptId': attempt_id
        })
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 500


@app.route('/proctoring/capture', methods=['POST'])
@require_auth
def capture_frame():
    """Capture and analyze a frame from webcam"""
    try:
        data = request.json
        attempt_id = data.get('attemptId')
        image_data = data.get('imageData')
        
        if not attempt_id or not image_data:
            return jsonify({'success': False, 'message': 'Missing data'}), 400
        
        # Decode base64 image
        try:
            image = decode_base64_image(image_data)
            
            # Validate image
            if image is None or image.size == 0:
                print(f"Warning: Invalid image data for attempt {attempt_id}")
                return jsonify({
                    'success': True,
                    'analysis': {
                        'faceDetected': False,
                        'multipleFaces': False,
                        'noFace': True,
                        'events': []
                    }
                })
        except Exception as decode_error:
            print(f"Error decoding image: {str(decode_error)}")
            return jsonify({
                'success': True,
                'analysis': {
                    'faceDetected': False,
                    'multipleFaces': False,
                    'noFace': True,
                    'events': []
                }
            })
        
        # Analyze frame with error handling
        try:
            analysis_result = analyze_frame(image, attempt_id)
        except Exception as analysis_error:
            print(f"Error analyzing frame: {str(analysis_error)}")
            # Return safe default analysis
            analysis_result = {
                'faceDetected': False,
                'multipleFaces': False,
                'noFace': True,
                'events': []
            }
        
        return jsonify({
            'success': True,
            'analysis': analysis_result
        })
    except Exception as e:
        print(f"Error in capture_frame: {str(e)}")
        # Return success with safe defaults to prevent blocking the exam
        return jsonify({
            'success': True,
            'analysis': {
                'faceDetected': False,
                'multipleFaces': False,
                'noFace': True,
                'events': []
            }
        })


@app.route('/proctoring/status/<int:attempt_id>', methods=['GET'])
def get_status(attempt_id):
    """Get proctoring status for an attempt"""
    try:
        logs = logger.get_logs(attempt_id)
        
        return jsonify({
            'success': True,
            'attemptId': attempt_id,
            'totalEvents': len(logs),
            'logs': logs
        })
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 500


def decode_base64_image(image_data):
    """Decode base64 image to OpenCV format"""
    # Remove data URL prefix if present
    if 'base64,' in image_data:
        image_data = image_data.split('base64,')[1]
    
    # Decode base64
    image_bytes = base64.b64decode(image_data)
    nparr = np.frombuffer(image_bytes, np.uint8)
    image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    return image


def analyze_frame(image, attempt_id):
    """Analyze frame for suspicious activities"""
    results = {
        'faceDetected': False,
        'multipleFaces': False,
        'noFace': False,
        'events': []
    }
    
    try:
        # Detect faces
        faces = face_detector.detect_faces(image)
        num_faces = len(faces)
        
        if num_faces == 0:
            # No face detected
            results['noFace'] = True
            absence_detector.record_absence(attempt_id)
            
            if absence_detector.is_suspicious(attempt_id):
                log_to_backend(attempt_id, 'NO_FACE', 
                              'Face not detected for extended period', 'HIGH')
                results['events'].append('NO_FACE_ALERT')
            else:
                log_to_backend(attempt_id, 'NO_FACE', 
                              'Face not detected', 'MEDIUM')
        
        elif num_faces == 1:
            # Single face detected (normal)
            results['faceDetected'] = True
            absence_detector.reset_absence(attempt_id)
            log_to_backend(attempt_id, 'FACE_DETECTED', 
                          'Student face detected', 'LOW')
        
        elif num_faces > 1:
            # Multiple faces detected (suspicious)
            results['multipleFaces'] = True
            results['faceCount'] = num_faces
            log_to_backend(attempt_id, 'MULTIPLE_FACES', 
                          f'{num_faces} faces detected in frame', 'HIGH')
            results['events'].append('MULTIPLE_FACES_ALERT')
    
    except Exception as e:
        print(f"Error in face detection: {str(e)}")
        # Return safe defaults on error
        results['noFace'] = True
    
    return results


def log_to_backend(attempt_id, event_type, description, severity):
    """Log proctoring event to backend"""
    try:
        # This would send to the Spring Boot backend
        logger.log_event(attempt_id, event_type, description, severity)
        
        # Send to backend
        try:
            requests.post(f"{BACKEND_URL}/proctoring/log", json={
                'attemptId': attempt_id,
                'eventType': event_type,
                'description': description,
                'severity': severity
            }, headers={'Content-Type': 'application/json'})
        except Exception as api_error:
            print(f"Failed to push to backend: {str(api_error)}")
    except Exception as e:
        print(f"Error logging to backend: {str(e)}")


@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'service': 'AI Proctoring Service',
        'version': '1.0.0'
    })


if __name__ == '__main__':
    print("=" * 50)
    print("AI Proctoring Service Starting...")
    print("Server running on: http://localhost:5000")
    print("=" * 50)
    app.run(host='0.0.0.0', port=5000, debug=True)
