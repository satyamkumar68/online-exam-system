# AI-Based Proctoring Service

## Overview
This is the AI-based proctoring service for the Online Examination System. It uses OpenCV for real-time face detection and monitoring during exams.

## Features
- Real-time face detection using Haar Cascade
- Multiple face detection (cheating detection)
- Absence detection (student not visible)
- Event logging and severity classification
- REST API for integration with backend

## Installation

### Prerequisites
- Python 3.8 or higher
- pip (Python package manager)

### Setup Steps

1. **Navigate to proctoring service directory**
```bash
cd proctoring-service
```

2. **Create virtual environment (recommended)**
```bash
python -m venv venv

# Activate on Windows
venv\Scripts\activate

# Activate on Linux/Mac
source venv/bin/activate
```

3. **Install dependencies**
```bash
pip install -r requirements.txt
```

## Running the Service

```bash
python app.py
```

The service will start on `http://localhost:5000`

## API Endpoints

### 1. Start Proctoring Session
```
POST /proctoring/start
Content-Type: application/json

{
    "attemptId": 1
}
```

### 2. Capture and Analyze Frame
```
POST /proctoring/capture
Content-Type: application/json

{
    "attemptId": 1,
    "imageData": "data:image/jpeg;base64,..."
}
```

### 3. Get Proctoring Status
```
GET /proctoring/status/<attempt_id>
```

### 4. Health Check
```
GET /health
```

## How It Works

### Face Detection
- Uses OpenCV's Haar Cascade Classifier
- Detects faces in real-time from webcam frames
- Classifies detection results:
  - 0 faces: No face detected (suspicious)
  - 1 face: Normal (student present)
  - 2+ faces: Multiple faces (cheating attempt)

### Event Logging
All events are logged with:
- Event type (FACE_DETECTED, NO_FACE, MULTIPLE_FACES)
- Timestamp
- Severity (LOW, MEDIUM, HIGH)
- Description

### Severity Levels
- **LOW**: Normal operation, single face detected
- **MEDIUM**: Temporary absence or minor issues
- **HIGH**: Multiple faces or extended absence

## Integration with Backend

The proctoring service can send events to the Spring Boot backend:

```python
requests.post(f"{BACKEND_URL}/proctoring/log", json={
    'attemptId': attempt_id,
    'eventType': event_type,
    'description': description,
    'severity': severity
})
```

## Module Structure

```
proctoring-service/
├── app.py                      # Main Flask application
├── requirements.txt            # Python dependencies
├── modules/
│   ├── face_detector.py       # Face detection using OpenCV
│   ├── multi_face_detector.py # Multiple face detection
│   ├── absence_detector.py    # Absence tracking
│   └── logger.py              # Event logging
└── README.md                  # This file
```

## Configuration

Edit `app.py` to configure:
- Backend URL: `BACKEND_URL = "http://localhost:8080/api"`
- Port: `app.run(port=5000)`
- Host: `app.run(host='0.0.0.0')`

## Troubleshooting

### Webcam Access Issues
- Ensure browser has webcam permissions
- Check if another application is using the webcam
- Try different browsers (Chrome recommended)

### Face Detection Not Working
- Ensure proper lighting
- Face should be clearly visible
- Check if Haar Cascade file is loaded correctly

### CORS Errors
- Ensure Flask-CORS is installed
- Check CORS configuration in `app.py`
- Verify frontend origin is allowed

## Future Enhancements

1. **Eye Tracking**: Detect if student is looking away
2. **Audio Detection**: Detect suspicious sounds
3. **Screen Monitoring**: Detect tab switching
4. **Deep Learning Models**: Use more accurate face detection (MTCNN, RetinaFace)
5. **Emotion Detection**: Detect stress or suspicious behavior
6. **Object Detection**: Detect phones or other devices

## Testing

Test the service using curl:

```bash
# Health check
curl http://localhost:5000/health

# Start proctoring
curl -X POST http://localhost:5000/proctoring/start \
  -H "Content-Type: application/json" \
  -d '{"attemptId": 1}'

# Get status
curl http://localhost:5000/proctoring/status/1
```

## Security Considerations

1. **Data Privacy**: Images are processed in real-time and not stored
2. **Encryption**: Use HTTPS in production
3. **Authentication**: Add API key authentication for production
4. **Rate Limiting**: Implement rate limiting to prevent abuse

## License
This is a final year project for educational purposes.

## Support
For issues or questions, refer to the main project documentation.
