# Database Design - ER Diagram Explanation

## Entity Relationship Diagram

```
┌─────────────────┐
│     USERS       │
│─────────────────│
│ PK: user_id     │
│ username        │
│ email           │
│ password        │
│ full_name       │
│ role            │
│ created_at      │
│ is_active       │
└─────────────────┘
        │
        │ 1
        │
        │ creates
        │
        │ M
        ▼
┌─────────────────┐         1        ┌─────────────────┐
│     EXAMS       │◄─────────────────│   QUESTIONS     │
│─────────────────│      contains    │─────────────────│
│ PK: exam_id     │                  │ PK: question_id │
│ exam_title      │                  │ FK: exam_id     │
│ exam_description│                  │ question_text   │
│ duration_minutes│                  │ question_type   │
│ total_marks     │                  │ marks           │
│ passing_marks   │                  │ correct_option  │
│ FK: created_by  │                  │ created_at      │
│ created_at      │                  └─────────────────┘
│ is_active       │                          │
│ start_time      │                          │ 1
│ end_time        │                          │
└─────────────────┘                          │ has
        │                                    │
        │ 1                                  │ M
        │                                    ▼
        │ attempted by              ┌─────────────────┐
        │                           │    OPTIONS      │
        │ M                         │─────────────────│
        ▼                           │ PK: option_id   │
┌─────────────────┐                │ FK: question_id │
│ EXAM_ATTEMPTS   │                │ option_text     │
│─────────────────│                │ option_label    │
│ PK: attempt_id  │                └─────────────────┘
│ FK: exam_id     │
│ FK: user_id     │
│ start_time      │
│ end_time        │
│ score           │
│ status          │
│ is_proctored    │
└─────────────────┘
        │
        │ 1
        │
        ├──────────────┬──────────────┐
        │              │              │
        │ has          │ has          │
        │              │              │
        │ M            │ M            │
        ▼              ▼              │
┌─────────────────┐  ┌─────────────────┐
│   RESPONSES     │  │ PROCTORING_LOGS │
│─────────────────│  │─────────────────│
│ PK: response_id │  │ PK: log_id      │
│ FK: attempt_id  │  │ FK: attempt_id  │
│ FK: question_id │  │ event_type      │
│ selected_option │  │ event_description│
│ is_correct      │  │ timestamp       │
│ answered_at     │  │ severity        │
└─────────────────┘  └─────────────────┘
```

## Relationships Explained

### 1. Users → Exams (1:M)
- **Relationship**: One admin user can create multiple exams
- **Foreign Key**: `exams.created_by` references `users.user_id`
- **Cardinality**: One-to-Many
- **Business Rule**: Each exam must be created by an admin user

### 2. Exams → Questions (1:M)
- **Relationship**: One exam contains multiple questions
- **Foreign Key**: `questions.exam_id` references `exams.exam_id`
- **Cardinality**: One-to-Many
- **Business Rule**: Questions belong to exactly one exam
- **Cascade**: Deleting an exam deletes all its questions

### 3. Questions → Options (1:M)
- **Relationship**: One question has multiple options (typically 4)
- **Foreign Key**: `options.question_id` references `questions.question_id`
- **Cardinality**: One-to-Many
- **Business Rule**: Each MCQ question has 2-4 options
- **Cascade**: Deleting a question deletes all its options

### 4. Users → Exam_Attempts (1:M)
- **Relationship**: One student can attempt multiple exams
- **Foreign Key**: `exam_attempts.user_id` references `users.user_id`
- **Cardinality**: One-to-Many
- **Business Rule**: Each attempt is by one student

### 5. Exams → Exam_Attempts (1:M)
- **Relationship**: One exam can be attempted by multiple students
- **Foreign Key**: `exam_attempts.exam_id` references `exams.exam_id`
- **Cardinality**: One-to-Many
- **Business Rule**: Each exam can have multiple attempts
- **Constraint**: Unique constraint on (user_id, exam_id) prevents multiple attempts

### 6. Exam_Attempts → Responses (1:M)
- **Relationship**: One exam attempt has multiple responses (one per question)
- **Foreign Key**: `responses.attempt_id` references `exam_attempts.attempt_id`
- **Cardinality**: One-to-Many
- **Business Rule**: Each response belongs to one attempt
- **Cascade**: Deleting an attempt deletes all responses

### 7. Questions → Responses (1:M)
- **Relationship**: One question can have multiple responses (from different attempts)
- **Foreign Key**: `responses.question_id` references `questions.question_id`
- **Cardinality**: One-to-Many
- **Constraint**: Unique constraint on (attempt_id, question_id) ensures one answer per question per attempt

### 8. Exam_Attempts → Proctoring_Logs (1:M)
- **Relationship**: One exam attempt generates multiple proctoring events
- **Foreign Key**: `proctoring_logs.attempt_id` references `exam_attempts.attempt_id`
- **Cardinality**: One-to-Many
- **Business Rule**: Proctoring logs track suspicious activities during exam
- **Cascade**: Deleting an attempt deletes all proctoring logs

## Normalization Analysis

### First Normal Form (1NF)
✅ **Achieved**: All tables have:
- Atomic values (no multi-valued attributes)
- Unique column names
- Primary keys defined

### Second Normal Form (2NF)
✅ **Achieved**: All tables have:
- No partial dependencies
- All non-key attributes fully dependent on primary key

### Third Normal Form (3NF)
✅ **Achieved**: All tables have:
- No transitive dependencies
- All non-key attributes depend only on primary key

**Example**: 
- In `exam_attempts`, `score` depends on `attempt_id` (not on `exam_id` or `user_id`)
- In `questions`, `correct_option` depends on `question_id` (not on `exam_id`)

## Indexes for Performance

### Primary Indexes (Automatic)
- All primary keys are automatically indexed

### Secondary Indexes (Explicit)
1. **users.username** - Fast login lookup
2. **users.email** - Fast email validation
3. **users.role** - Filter by user type
4. **exams.is_active** - Show only active exams
5. **questions.exam_id** - Fetch questions by exam
6. **exam_attempts.user_id** - Student's exam history
7. **exam_attempts.status** - Filter by attempt status
8. **responses.attempt_id** - Fetch all answers for an attempt
9. **proctoring_logs.attempt_id** - Fetch logs for an attempt
10. **proctoring_logs.severity** - Filter high-severity events

## Constraints Summary

### Primary Keys
- Auto-increment integers for all tables
- Ensures unique identification

### Foreign Keys
- Enforce referential integrity
- CASCADE delete for dependent data
- Prevents orphaned records

### Unique Constraints
1. `users.username` - No duplicate usernames
2. `users.email` - No duplicate emails
3. `exam_attempts(user_id, exam_id)` - One attempt per student per exam

### Check Constraints (via ENUM)
1. `users.role` - Only ADMIN or STUDENT
2. `questions.question_type` - Only MCQ or TRUE_FALSE
3. `exam_attempts.status` - Only IN_PROGRESS, COMPLETED, AUTO_SUBMITTED
4. `proctoring_logs.event_type` - Predefined event types
5. `proctoring_logs.severity` - Only LOW, MEDIUM, HIGH

### NOT NULL Constraints
- Critical fields like username, password, exam_title, question_text
- Ensures data completeness

## Views for Analytics

### 1. student_results_view
**Purpose**: Simplified result viewing for students and admins

**Columns**:
- Student details (user_id, full_name, email)
- Exam details (exam_title)
- Attempt details (attempt_id, start_time, end_time)
- Performance (score, total_marks, status, result)

**Use Case**: Display results dashboard

### 2. exam_statistics_view
**Purpose**: Aggregate statistics for each exam

**Columns**:
- Exam details (exam_id, exam_title)
- Statistics (total_attempts, average_score, highest_score, lowest_score)
- Pass/Fail counts

**Use Case**: Admin analytics dashboard

## Data Integrity Rules

1. **User Authentication**
   - Passwords must be BCrypt encrypted
   - Email must be valid format
   - Username must be unique

2. **Exam Scheduling**
   - start_time must be before end_time
   - duration_minutes must be positive
   - passing_marks ≤ total_marks

3. **Question Validation**
   - correct_option must match one of the option_labels
   - marks must be positive
   - Each question must have at least 2 options

4. **Attempt Validation**
   - Student cannot attempt same exam twice (unique constraint)
   - end_time must be after start_time
   - score must be between 0 and total_marks

5. **Response Validation**
   - selected_option must be one of the valid options
   - One response per question per attempt (unique constraint)

## Security Considerations

1. **Password Storage**: BCrypt hashing with salt
2. **SQL Injection Prevention**: Parameterized queries via JPA
3. **Data Isolation**: Role-based access control
4. **Audit Trail**: Timestamps on all critical tables
5. **Soft Delete**: is_active flag instead of hard delete for users/exams
