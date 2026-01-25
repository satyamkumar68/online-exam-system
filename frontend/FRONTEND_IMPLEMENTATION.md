# Complete Frontend Implementation

This document contains all remaining HTML pages and JavaScript for the Online Examination System frontend.

## Student Dashboard
File: `student/dashboard.html`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <a href="#" class="navbar-brand">📚 Exam Portal - Student</a>
            <div class="navbar-menu" id="user-info"></div>
        </div>
    </nav>

    <div class="container">
        <h2>Available Exams</h2>
        <div id="alert-container"></div>
        <div id="exams-container" class="grid grid-3"></div>
    </div>

    <script src="../js/api.js"></script>
    <script src="../js/auth.js"></script>
    <script src="../js/student.js"></script>
</body>
</html>
```

## Student JavaScript
File: `js/student.js`

```javascript
// Check authentication
checkAuth('STUDENT');

// Load available exams
async function loadExams() {
    try {
        const data = await apiRequest('/student/exams');
        const container = document.getElementById('exams-container');
        
        if (data.success && data.data.length > 0) {
            container.innerHTML = data.data.map(exam => `
                <div class="card">
                    <h3>${exam.examTitle}</h3>
                    <p>${exam.examDescription}</p>
                    <div style="margin: 15px 0;">
                        <p><strong>Duration:</strong> ${formatDuration(exam.durationMinutes)}</p>
                        <p><strong>Total Marks:</strong> ${exam.totalMarks}</p>
                        <p><strong>Passing Marks:</strong> ${exam.passingMarks}</p>
                    </div>
                    <button onclick="startExam(${exam.examId})" class="btn btn-primary" style="width: 100%;">
                        Start Exam
                    </button>
                </div>
            `).join('');
        } else {
            container.innerHTML = '<p class="text-center">No exams available at the moment.</p>';
        }
    } catch (error) {
        showAlert('Failed to load exams', 'error');
    }
}

async function startExam(examId) {
    if (!confirm('Are you sure you want to start this exam? Timer will begin immediately.')) {
        return;
    }
    
    try {
        const data = await apiRequest(`/student/exams/${examId}/start`, {
            method: 'POST'
        });
        
        if (data.success) {
            localStorage.setItem('attemptId', data.data.attemptId);
            window.location.href = 'exam-interface.html';
        } else {
            showAlert(data.message, 'error');
        }
    } catch (error) {
        showAlert('Failed to start exam', 'error');
    }
}

// Load exams on page load
document.addEventListener('DOMContentLoaded', loadExams);
```

## Exam Interface
File: `student/exam-interface.html`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Exam in Progress</title>
    <link rel="stylesheet" href="../css/style.css">
    <style>
        #timer {
            position: fixed;
            top: 80px;
            right: 20px;
            background: var(--danger-color);
            color: white;
            padding: 15px 25px;
            border-radius: 8px;
            font-size: 24px;
            font-weight: bold;
            box-shadow: var(--shadow-lg);
            z-index: 100;
        }
        
        #proctoring-indicator {
            position: fixed;
            top: 80px;
            left: 20px;
            background: var(--secondary-color);
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            font-size: 14px;
            box-shadow: var(--shadow-lg);
            z-index: 100;
        }
        
        .question-card {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: var(--shadow-md);
            margin-bottom: 20px;
        }
        
        .option-label {
            display: block;
            padding: 15px;
            margin: 10px 0;
            border: 2px solid var(--border-color);
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .option-label:hover {
            border-color: var(--primary-color);
            background: var(--light-bg);
        }
        
        .option-label input[type="radio"]:checked + span {
            color: var(--primary-color);
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div id="timer">00:00</div>
    <div id="proctoring-indicator">🎥 Proctoring Active</div>
    
    <div class="container" style="margin-top: 100px;">
        <div id="alert-container"></div>
        
        <div id="questions-container"></div>
        
        <div class="text-center mt-3">
            <button onclick="submitExam()" class="btn btn-danger" style="padding: 15px 40px; font-size: 18px;">
                Submit Exam
            </button>
        </div>
    </div>
    
    <video id="webcam" style="display: none;" autoplay></video>
    
    <script src="../js/api.js"></script>
    <script src="../js/auth.js"></script>
    <script src="../js/exam.js"></script>
    <script src="../js/timer.js"></script>
</body>
</html>
```

## Exam JavaScript
File: `js/exam.js`

```javascript
checkAuth('STUDENT');

const attemptId = localStorage.getItem('attemptId');
if (!attemptId) {
    window.location.href = 'dashboard.html';
}

let questions = [];
let answers = {};

// Load exam questions
async function loadQuestions() {
    try {
        const data = await apiRequest(`/student/attempts/${attemptId}/questions`);
        
        if (data.success) {
            questions = data.data;
            renderQuestions();
        }
    } catch (error) {
        showAlert('Failed to load questions', 'error');
    }
}

function renderQuestions() {
    const container = document.getElementById('questions-container');
    
    container.innerHTML = questions.map((q, index) => `
        <div class="question-card">
            <h3>Question ${index + 1}</h3>
            <p style="font-size: 18px; margin: 20px 0;">${q.questionText}</p>
            
            <div id="options-${q.questionId}">
                ${renderOptions(q)}
            </div>
        </div>
    `).join('');
}

function renderOptions(question) {
    // Fetch options from API or use embedded options
    const options = question.options || [
        {optionLabel: 'A', optionText: 'Option A'},
        {optionLabel: 'B', optionText: 'Option B'},
        {optionLabel: 'C', optionText: 'Option C'},
        {optionLabel: 'D', optionText: 'Option D'}
    ];
    
    return options.map(opt => `
        <label class="option-label">
            <input type="radio" name="question-${question.questionId}" 
                   value="${opt.optionLabel}" 
                   onchange="saveAnswer(${question.questionId}, '${opt.optionLabel}')">
            <span>${opt.optionLabel}. ${opt.optionText}</span>
        </label>
    `).join('');
}

async function saveAnswer(questionId, selectedOption) {
    answers[questionId] = selectedOption;
    
    try {
        await apiRequest(`/student/attempts/${attemptId}/answer`, {
            method: 'POST',
            body: JSON.stringify({ questionId, selectedOption })
        });
    } catch (error) {
        console.error('Failed to save answer:', error);
    }
}

async function submitExam() {
    if (!confirm('Are you sure you want to submit the exam? This action cannot be undone.')) {
        return;
    }
    
    try {
        const data = await apiRequest(`/student/attempts/${attemptId}/submit`, {
            method: 'POST'
        });
        
        if (data.success) {
            localStorage.removeItem('attemptId');
            alert('Exam submitted successfully!');
            window.location.href = 'results.html';
        }
    } catch (error) {
        showAlert('Failed to submit exam', 'error');
    }
}

// Initialize webcam for proctoring
async function initProctoring() {
    try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true });
        const video = document.getElementById('webcam');
        video.srcObject = stream;
        
        // Capture frame every 5 seconds for proctoring
        setInterval(() => captureFrame(video), 5000);
    } catch (error) {
        console.error('Webcam access denied:', error);
    }
}

function captureFrame(video) {
    const canvas = document.createElement('canvas');
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext('2d').drawImage(video, 0, 0);
    
    // Convert to base64 and send to proctoring service
    const imageData = canvas.toDataURL('image/jpeg');
    sendToProctoringService(imageData);
}

async function sendToProctoringService(imageData) {
    // Send to Python proctoring service
    try {
        await fetch('http://localhost:5000/proctoring/capture', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ attemptId, imageData })
        });
    } catch (error) {
        console.error('Proctoring service error:', error);
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadQuestions();
    initProctoring();
});
```

## Timer JavaScript
File: `js/timer.js`

```javascript
let timeRemaining = 3600; // 60 minutes in seconds
let timerInterval;

function startTimer(durationMinutes) {
    timeRemaining = durationMinutes * 60;
    
    timerInterval = setInterval(() => {
        timeRemaining--;
        updateTimerDisplay();
        
        if (timeRemaining <= 0) {
            clearInterval(timerInterval);
            autoSubmitExam();
        } else if (timeRemaining === 300) {
            alert('⚠️ 5 minutes remaining!');
        }
    }, 1000);
}

function updateTimerDisplay() {
    const minutes = Math.floor(timeRemaining / 60);
    const seconds = timeRemaining % 60;
    
    const display = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    document.getElementById('timer').textContent = display;
    
    if (timeRemaining <= 300) {
        document.getElementById('timer').style.background = var(--danger-color);
    }
}

async function autoSubmitExam() {
    alert('Time is up! Exam will be auto-submitted.');
    await submitExam();
}

// Start timer with exam duration
startTimer(60); // 60 minutes
```

## Admin Dashboard
File: `admin/dashboard.html`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <a href="#" class="navbar-brand">📚 Exam Portal - Admin</a>
            <div class="navbar-menu">
                <a href="dashboard.html" class="navbar-link">Dashboard</a>
                <a href="manage-exams.html" class="navbar-link">Manage Exams</a>
                <a href="view-results.html" class="navbar-link">Results</a>
                <div id="user-info"></div>
            </div>
        </div>
    </nav>

    <div class="container">
        <h2>Dashboard Overview</h2>
        
        <div class="grid grid-4">
            <div class="card text-center">
                <h3 style="color: var(--primary-color);">📝</h3>
                <h2 id="total-exams">0</h2>
                <p>Total Exams</p>
            </div>
            
            <div class="card text-center">
                <h3 style="color: var(--secondary-color);">✅</h3>
                <h2 id="active-exams">0</h2>
                <p>Active Exams</p>
            </div>
            
            <div class="card text-center">
                <h3 style="color: var(--warning-color);">👥</h3>
                <h2 id="total-students">0</h2>
                <p>Total Students</p>
            </div>
            
            <div class="card text-center">
                <h3 style="color: var(--danger-color);">📊</h3>
                <h2 id="total-attempts">0</h2>
                <p>Total Attempts</p>
            </div>
        </div>

        <div class="card mt-3">
            <h3>Recent Exams</h3>
            <div id="recent-exams"></div>
        </div>
    </div>

    <script src="../js/api.js"></script>
    <script src="../js/auth.js"></script>
    <script src="../js/admin.js"></script>
</body>
</html>
```

## Admin JavaScript
File: `js/admin.js`

```javascript
checkAuth('ADMIN');

async function loadDashboard() {
    try {
        const data = await apiRequest('/admin/dashboard');
        
        if (data.success) {
            document.getElementById('total-exams').textContent = data.data.totalExams || 0;
            document.getElementById('active-exams').textContent = data.data.activeExams || 0;
        }
    } catch (error) {
        showAlert('Failed to load dashboard', 'error');
    }
}

async function loadExams() {
    try {
        const data = await apiRequest('/admin/exams');
        
        if (data.success) {
            renderExamsTable(data.data);
        }
    } catch (error) {
        showAlert('Failed to load exams', 'error');
    }
}

function renderExamsTable(exams) {
    const container = document.getElementById('exams-table');
    
    container.innerHTML = `
        <table class="table">
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Duration</th>
                    <th>Total Marks</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${exams.map(exam => `
                    <tr>
                        <td>${exam.examTitle}</td>
                        <td>${formatDuration(exam.durationMinutes)}</td>
                        <td>${exam.totalMarks}</td>
                        <td>
                            <span class="badge ${exam.isActive ? 'badge-success' : 'badge-danger'}">
                                ${exam.isActive ? 'Active' : 'Inactive'}
                            </span>
                        </td>
                        <td>
                            <button onclick="editExam(${exam.examId})" class="btn btn-primary" style="padding: 5px 10px;">Edit</button>
                            <button onclick="deleteExam(${exam.examId})" class="btn btn-danger" style="padding: 5px 10px;">Delete</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

async function deleteExam(examId) {
    if (!confirm('Are you sure you want to delete this exam?')) {
        return;
    }
    
    try {
        const data = await apiRequest(`/admin/exams/${examId}`, {
            method: 'DELETE'
        });
        
        if (data.success) {
            showAlert('Exam deleted successfully', 'success');
            loadExams();
        }
    } catch (error) {
        showAlert('Failed to delete exam', 'error');
    }
}

document.addEventListener('DOMContentLoaded', loadDashboard);
```

## Running the Frontend

1. Open `login.html` in a web browser
2. Use demo credentials to login
3. Navigate through the interface

**Note:** Ensure the backend server is running on `http://localhost:8080`

## Frontend File Structure

```
frontend/
├── index.html
├── login.html
├── register.html
├── admin/
│   ├── dashboard.html
│   ├── manage-exams.html
│   ├── create-exam.html
│   ├── manage-questions.html
│   ├── view-results.html
│   └── proctoring-logs.html
├── student/
│   ├── dashboard.html
│   ├── exam-list.html
│   ├── exam-interface.html
│   └── results.html
├── css/
│   ├── style.css
│   ├── admin.css
│   └── student.css
└── js/
    ├── auth.js
    ├── api.js
    ├── admin.js
    ├── student.js
    ├── exam.js
    └── timer.js
```
