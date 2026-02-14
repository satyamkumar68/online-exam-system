# Online Examination System (Rewritten)

## Overview
This project has been completely rewritten to ensure stability and correct configuration.

## Architecture
- **Backend**: Spring Boot 3 (Port 8081) - IPv4 `127.0.0.1`
- **Frontend**: HTML5 + JavaScript (Port 7001) - Connects to `127.0.0.1:8081`
- **Proctoring**: Python Flask (Port 7003) - Connects to `127.0.0.1:8081`

## How to Run
1.  Run `start_all.bat`.
2.  Wait for the Backend to start (look for "Started OnlineExamSystemApplication").
3.  Open browser to `http://127.0.0.1:7001`.

## Credentials
-   **Sign Up**: Create a new account on the login page.
-   **Login**: Use the credentials you just created.

## Notes
-   The old project code is archived in `_archive_20260214`.
-   All internal communication uses `127.0.0.1` to avoid localhost IPv6 issues.
