// Authentication JavaScript

// Login form handler
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const alertContainer = document.getElementById('alert-container');

    try {
        const response = await fetch(API_BASE_URL + '/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (data.success) {
            // Save token and user data
            saveAuth(data.data.token, {
                userId: data.data.userId,
                username: data.data.username,
                email: data.data.email,
                fullName: data.data.fullName,
                role: data.data.role
            });

            // Redirect based on role
            if (data.data.role === 'ADMIN') {
                window.location.href = 'admin/dashboard.html';
            } else {
                window.location.href = 'student/dashboard.html';
            }
        } else {
            displayAlert('alert-container', data.message, 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        displayAlert('alert-container', 'Login failed. Please check your credentials.', 'error');
    }
});

// Logout function
function logout() {
    clearAuth();
    window.location.href = '/login.html';
}

// Check authentication on protected pages
function checkAuth(requiredRole = null) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return false;
    }

    if (requiredRole) {
        const user = getUser();
        if (user.role !== requiredRole) {
            window.location.href = '/login.html';
            return false;
        }
    }

    return true;
}

// Display user info in navbar
function displayUserInfo() {
    const user = getUser();
    if (!user) return;

    const userInfoElement = document.getElementById('user-info');
    if (userInfoElement) {
        userInfoElement.innerHTML = `
            <span style="color: white; margin-right: 15px;">👤 ${user.fullName}</span>
            <button onclick="logout()" class="btn btn-danger" style="padding: 8px 16px;">Logout</button>
        `;
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    displayUserInfo();
});
