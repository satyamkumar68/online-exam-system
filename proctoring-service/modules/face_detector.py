"""
Face Detection Module using OpenCV
"""

import cv2
import os

class FaceDetector:
    def __init__(self):
        """Initialize face detector with Haar Cascade"""
        # Path to Haar Cascade XML file
        cascade_path = cv2.data.haarcascades + 'haarcascade_frontalface_default.xml'
        
        if not os.path.exists(cascade_path):
            print(f"Warning: Cascade file not found at {cascade_path}")
            self.face_cascade = None
        else:
            self.face_cascade = cv2.CascadeClassifier(cascade_path)
    
    def detect_faces(self, image):
        """
        Detect faces in an image
        
        Args:
            image: OpenCV image (numpy array)
        
        Returns:
            List of face rectangles [(x, y, w, h), ...]
        """
        if self.face_cascade is None:
            return []
        
        # Convert to grayscale for better detection
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        
        # Detect faces
        faces = self.face_cascade.detectMultiScale(
            gray,
            scaleFactor=1.1,
            minNeighbors=5,
            minSize=(30, 30),
            flags=cv2.CASCADE_SCALE_IMAGE
        )
        
        return faces
    
    def draw_faces(self, image, faces):
        """
        Draw rectangles around detected faces
        
        Args:
            image: OpenCV image
            faces: List of face rectangles
        
        Returns:
            Image with rectangles drawn
        """
        for (x, y, w, h) in faces:
            cv2.rectangle(image, (x, y), (x+w, y+h), (0, 255, 0), 2)
        
        return image
    
    def get_face_count(self, image):
        """
        Get number of faces in image
        
        Args:
            image: OpenCV image
        
        Returns:
            Number of faces detected
        """
        faces = self.detect_faces(image)
        return len(faces)
