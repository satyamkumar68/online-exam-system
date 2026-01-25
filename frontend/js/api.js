// API Configuration (environment-based)
const API_BASE_URL = window.ENV?.API_URL || 'http://localhost:8081/api';

// API request timeout (30 seconds)
const REQUEST_TIMEOUT = 30000;

// Get JWT token from localStorage
function getToken() {
    return localStorage.getItem('token');
}

// Get user data from localStorage
function getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

// Save authentication data
function saveAuth(token, user) {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
}

// Clear authentication data
function clearAuth() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
}

// Check if user is authenticated
function isAuthenticated() {
    return getToken() !== null;
}

// Check if user is admin
function isAdmin() {
    const user = getUser();
    return user && user.role === 'ADMIN';
}

// Check if user is student
function isStudent() {
    const user = getUser();
    return user && user.role === 'STUDENT';
}

// Make authenticated API request with timeout and error handling
async function apiRequest(endpoint, options = {}) {
    const token = getToken();

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        // Create abort controller for timeout
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT);

        const response = await fetch(API_BASE_URL + endpoint, {
            ...options,
            headers,
            signal: controller.signal
        });

        clearTimeout(timeoutId);

        // Handle different HTTP status codes
        if (response.status === 401) {
            clearAuth();
            window.location.href = '/login.html';
            throw new Error('Session expired. Please login again.');
        }

        if (response.status === 403) {
            throw new Error('You do not have permission to perform this action.');
        }

        if (response.status === 404) {
            throw new Error('Resource not found.');
        }

        if (response.status >= 500) {
            throw new Error('Server error. Please try again later.');
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Request failed with status ${response.status}`);
        }

        return await response.json();

    } catch (error) {
        // Handle network errors
        if (error.name === 'AbortError') {
            throw new Error('Request timeout. Please check your connection and try again.');
        }

        if (error.name === 'TypeError') {
            throw new Error('Network error. Please check your internet connection.');
        }

        // Re-throw other errors
        throw error;
    }
}

// Show alert message (XSS-safe)
function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('alert-container');
    if (!alertContainer) return;

    // Create alert element safely (prevents XSS)
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message; // Safe from XSS attacks

    // Clear previous alerts
    alertContainer.innerHTML = '';
    alertContainer.appendChild(alertDiv);

    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// Create XSS-safe alert element (reusable helper)
function createSafeAlert(message, type = 'error') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;
    return alertDiv;
}

// Display alert in container (XSS-safe)
function displayAlert(containerId, message, type = 'error') {
    const container = document.getElementById(containerId);
    if (!container) return;

    const alertDiv = createSafeAlert(message, type);
    container.innerHTML = '';
    container.appendChild(alertDiv);
}

// Format date
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
}

// Format duration (minutes to hours:minutes)
function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return hours > 0 ? `${hours}h ${mins}m` : `${mins}m`;
}
