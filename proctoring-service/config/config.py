"""
Configuration module for AI Proctoring Service
Loads environment variables for secure configuration
"""

import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

class Config:
    """Application configuration"""
    
    # Flask Configuration
    DEBUG = os.getenv('DEBUG', 'True').lower() == 'true'
    HOST = os.getenv('HOST', '0.0.0.0')
    PORT = int(os.getenv('PORT', 5000))
    
    # Backend API Configuration
    BACKEND_URL = os.getenv('BACKEND_URL', 'http://localhost:8081/api')
    
    # Face Detection Configuration
    FACE_DETECTION_SCALE_FACTOR = float(os.getenv('FACE_DETECTION_SCALE_FACTOR', '1.1'))
    FACE_DETECTION_MIN_NEIGHBORS = int(os.getenv('FACE_DETECTION_MIN_NEIGHBORS', '5'))
    FACE_DETECTION_MIN_SIZE = tuple(map(int, os.getenv('FACE_DETECTION_MIN_SIZE', '30,30').split(',')))
    
    # Absence Detection Configuration
    ABSENCE_THRESHOLD_COUNT = int(os.getenv('ABSENCE_THRESHOLD_COUNT', '3'))
    ABSENCE_RESET_TIMEOUT = int(os.getenv('ABSENCE_RESET_TIMEOUT', '60'))
    
    # Logging Configuration
    LOG_LEVEL = os.getenv('LOG_LEVEL', 'INFO')
    
    # Redis Configuration (for future use)
    REDIS_HOST = os.getenv('REDIS_HOST', 'localhost')
    REDIS_PORT = int(os.getenv('REDIS_PORT', 6379))
    REDIS_DB = int(os.getenv('REDIS_DB', 0))
    
    @staticmethod
    def validate():
        """Validate required configuration"""
        required_vars = []
        
        missing = [var for var in required_vars if not os.getenv(var)]
        
        if missing:
            raise ValueError(f"Missing required environment variables: {', '.join(missing)}")
        
        return True
