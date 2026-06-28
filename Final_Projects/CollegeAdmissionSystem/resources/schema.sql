-- ============================================================
-- College Admission Management System - Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS college_admission;
USE college_admission;

-- -------------------------------------------------------
-- Table: Students
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS Students (
    student_id      INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone           VARCHAR(15),
    date_of_birth   DATE         NOT NULL,
    gender          ENUM('Male','Female','Other') NOT NULL,
    address         TEXT,
    city            VARCHAR(50),
    state           VARCHAR(50),
    pincode         VARCHAR(10),
    tenth_marks     DECIMAL(5,2) NOT NULL COMMENT 'Percentage',
    twelfth_marks   DECIMAL(5,2) NOT NULL COMMENT 'Percentage',
    entrance_score  DECIMAL(5,2) DEFAULT 0 COMMENT 'Entrance exam score out of 100',
    merit_score     DECIMAL(6,3) GENERATED ALWAYS AS (
                        (tenth_marks * 0.20) + (twelfth_marks * 0.50) + (entrance_score * 0.30)
                    ) STORED COMMENT 'Weighted merit: 10th=20%, 12th=50%, Entrance=30%',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- Table: Courses
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS Courses (
    course_id       INT AUTO_INCREMENT PRIMARY KEY,
    course_code     VARCHAR(20)  NOT NULL UNIQUE,
    course_name     VARCHAR(100) NOT NULL,
    department      VARCHAR(100) NOT NULL,
    duration_years  INT          NOT NULL DEFAULT 4,
    total_seats     INT          NOT NULL,
    available_seats INT          NOT NULL,
    cut_off_merit   DECIMAL(6,3) NOT NULL COMMENT 'Minimum merit score required',
    fee_per_year    DECIMAL(10,2) NOT NULL,
    description     TEXT,
    is_active       TINYINT(1)   DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- Table: Applications
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS Applications (
    application_id  INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT          NOT NULL,
    course_id       INT          NOT NULL,
    application_date TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    status          ENUM('Pending','Approved','Rejected','Waitlisted') DEFAULT 'Pending',
    admin_remarks   TEXT,
    reviewed_by     VARCHAR(100),
    reviewed_at     TIMESTAMP   NULL,
    documents_verified TINYINT(1) DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES Courses(course_id)   ON DELETE CASCADE,
    UNIQUE KEY unique_application (student_id, course_id)
);

-- -------------------------------------------------------
-- Table: Admins
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS Admins (
    admin_id        INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    is_active       TINYINT(1)   DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- Seed: Default Admin (password: admin123)
-- -------------------------------------------------------
INSERT INTO Admins (username, password_hash, full_name, email) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 'System Administrator', 'admin@college.edu');

-- -------------------------------------------------------
-- Seed: Sample Courses
-- -------------------------------------------------------
INSERT INTO Courses (course_code, course_name, department, duration_years, total_seats, available_seats, cut_off_merit, fee_per_year, description) VALUES
('CS101',  'B.Tech Computer Science',          'Engineering',        4, 60, 60, 75.000, 120000.00, 'Core CS with specialization in AI/ML'),
('EC101',  'B.Tech Electronics & Comm.',       'Engineering',        4, 60, 60, 70.000, 110000.00, 'Electronics, VLSI and Communication'),
('ME101',  'B.Tech Mechanical Engineering',    'Engineering',        4, 60, 60, 65.000, 100000.00, 'Core Mechanical with CAD/CAM'),
('CE101',  'B.Tech Civil Engineering',         'Engineering',        4, 60, 60, 60.000, 95000.00,  'Structural and Environmental Engineering'),
('MBA101', 'Master of Business Administration','Management',         2, 40, 40, 70.000, 150000.00, 'General MBA with specializations'),
('MCA101', 'Master of Computer Applications', 'Computer Science',   3, 40, 40, 72.000, 100000.00, 'Advanced application development'),
('BSC101', 'B.Sc. Physics',                   'Science',            3, 40, 40, 55.000, 60000.00,  'Pure Physics with lab'),
('BCA101', 'Bachelor of Computer Applications','Computer Science',  3, 60, 60, 58.000, 75000.00,  'Application-oriented CS program');
