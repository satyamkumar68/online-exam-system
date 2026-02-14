from flask import Flask, jsonify, request
import requests
import os
import logging

app = Flask(__name__)

# Configuration
PORT = 7003
BACKEND_URL = "http://127.0.0.1:8081/api"

# Logging setup
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@app.route('/')
def home():
    return jsonify({
        "status": "healthy",
        "service": "AI Proctoring Service",
        "backend_url": BACKEND_URL
    })

@app.route('/start_proctoring', methods=['POST'])
def start_proctoring():
    try:
        data = request.json
        user_id = data.get('user_id')
        exam_id = data.get('exam_id')
        
        logger.info(f"Starting proctoring for User {user_id}, Exam {exam_id}")
        
        # In a real app, this would start a background thread for webcam monitoring
        # For now, we just verify connection to backend
        
        return jsonify({
            "status": "started",
            "message": "Proctoring session initialized"
        })
        
    except Exception as e:
        logger.error(f"Error starting proctoring: {str(e)}")
        return jsonify({"status": "error", "message": str(e)}), 500

import cv2
import numpy as np
import base64

# Load Haar Cascade for face detection
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

@app.route('/process_frame', methods=['POST', 'OPTIONS'])
def process_frame():
    if request.method == 'OPTIONS':
        # Handle CORS preflight
        response = jsonify({'status': 'ok'})
        response.headers.add("Access-Control-Allow-Origin", "*")
        response.headers.add("Access-Control-Allow-Headers", "Content-Type")
        return response

    try:
        data = request.json
        image_data = data.get('image')
        
        if not image_data:
            return jsonify({"status": "error", "message": "No image data"}), 400

        # Decode base64 image
        # Remove data:image/jpeg;base64, prefix if present
        if ',' in image_data:
            image_data = image_data.split(',')[1]
            
        decoded_data = base64.b64decode(image_data)
        np_data = np.frombuffer(decoded_data, np.uint8)
        frame = cv2.imdecode(np_data, cv2.IMREAD_COLOR)

        if frame is None:
             logger.error("Failed to decode image")
             return jsonify({"status": "error", "message": "Failed to decode image"}), 400

        # Save for debugging (overwrite each time to save space)
        cv2.imwrite("debug_last_received.jpg", frame)

        # Face Detection
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        
        # Check brightness
        brightness = np.mean(gray)
        
        # Try detecting with RELAXED scale factors
        faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=4, minSize=(30, 30))
        
        face_count = len(faces)
        
        # Draw rectangles for debug
        for (x, y, w, h) in faces:
            cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 0), 2)
        cv2.imwrite("debug_last_processed.jpg", frame)
        
        status = "clean"
        message = "Session normal"
        
        if brightness < 50:
            status = "warning"
            message = "Lighting too low. Please check your lights."
        elif face_count == 0:
            status = "warning"
            message = "No face detected. Please look at the camera."
        elif face_count > 1:
            status = "violation"
            message = "Multiple faces detected!"

        logger.info(f"Proctoring: {face_count} faces. Brightness: {brightness:.2f}. Status: {status}")

        response = jsonify({
            "status": status,
            "message": message,
            "face_count": face_count,
            "brightness": brightness
        })
        response.headers.add("Access-Control-Allow-Origin", "*")
        return response

    except Exception as e:
        logger.error(f"Error processing frame: {str(e)}")
        response = jsonify({"status": "error", "message": str(e)})
        response.headers.add("Access-Control-Allow-Origin", "*")
        return response, 500

if __name__ == '__main__':
    print(f"Starting Proctoring Service on port {PORT}...")
    app.run(host='127.0.0.1', port=PORT, debug=True)
