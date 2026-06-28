# College Admission Management System — User Guide

## Table of Contents
1. [System Overview](#overview)
2. [Prerequisites & Setup](#setup)
3. [Database Setup](#database)
4. [Building & Running](#build)
5. [Merit Calculation Formula](#merit)
6. [Student Portal Guide](#student)
7. [Admin Panel Guide](#admin)
8. [Generated Reports](#reports)
9. [Project Structure](#structure)
10. [Troubleshooting](#troubleshooting)

---

## 1. System Overview <a name="overview"></a>

The **College Admission Management System** is a Java console application that automates:
- Student registration with academic marks entry
- Automatic merit score calculation
- Course browsing and application submission
- Admin approval/rejection based on merit cut-offs
- Bulk auto-processing of applications
- CSV and formatted text report generation

**Tech Stack:** Java 17 · JDBC · MySQL 8+ · Maven

---

## 2. Prerequisites & Setup <a name="setup"></a>

| Tool           | Version  | Download                             |
|----------------|----------|--------------------------------------|
| Java JDK       | 17+      | https://adoptium.net                 |
| Maven          | 3.8+     | https://maven.apache.org             |
| MySQL Server   | 8.0+     | https://dev.mysql.com/downloads      |
| MySQL Workbench| Optional | For visual DB inspection             |

---

## 3. Database Setup <a name="database"></a>

**Step 1:** Start MySQL and run the schema:

```sql
mysql -u root -p < resources/schema.sql
```

Or open `resources/schema.sql` in MySQL Workbench and execute it.

This creates the `college_admission` database with:
- **Students** — stores registrant info and auto-computed merit score
- **Courses** — available programs with seats and cut-off values
- **Applications** — links students to courses with status tracking
- **Admins** — admin login credentials

Default admin credentials seeded:
- **Username:** `admin`
- **Password:** `admin123`

**Step 2:** Edit `resources/db.properties` to match your MySQL setup:

```properties
db.url=jdbc:mysql://localhost:3306/college_admission?useSSL=false&serverTimezone=UTC
db.user=root
db.password=YOUR_PASSWORD
```

---

## 4. Building & Running <a name="build"></a>

```bash
# Clone / unzip the project
cd CollegeAdmissionSystem

# Build a fat executable JAR (includes MySQL connector)
mvn clean package

# Run the application
java -jar target/CollegeAdmissionSystem.jar
```

The system will test the DB connection on startup and exit with an error message if it cannot connect.

---

## 5. Merit Calculation Formula <a name="merit"></a>

Merit is calculated automatically from three components:

```
Merit Score = (10th Marks × 0.20) + (12th Marks × 0.50) + (Entrance Score × 0.30)
```

| Component      | Weight |
|----------------|--------|
| 10th Marks (%) | 20%    |
| 12th Marks (%) | 50%    |
| Entrance Score | 30%    |

**Example:**
- 10th: 85% → 85 × 0.20 = 17.00
- 12th: 80% → 80 × 0.50 = 40.00
- Entrance: 70  → 70 × 0.30 = 21.00
- **Merit Score = 78.00**

The formula is stored as a **MySQL generated column** (automatically recalculated on update) and mirrored in `Student.calculateMerit()` for display.

---

## 6. Student Portal Guide <a name="student"></a>

Access via **Main Menu → Option 1**.

### Register New Student (Option 1)
Enter personal details and academic marks. The system will display your assigned Student ID and computed merit score immediately after registration.

### View Available Courses (Option 2)
Lists all active courses with:
- Course code and name
- Department
- Available seats
- Merit cut-off required
- Annual fee

### Apply for a Course (Option 3)
1. Enter your Student ID
2. System displays your merit score
3. View the course list
4. Enter the Course ID you want to apply for
5. Receive an Application ID for tracking

> **Note:** You can only apply once per course. Applications are subject to admin review.

### Check Application Status (Option 4)
Enter your Student ID to see the status of all your applications:
- **Pending** — Awaiting admin review
- **Approved** — You have been admitted
- **Rejected** — Application declined (with reason)
- **Waitlisted** — On hold, subject to seat availability

### View My Merit Score (Option 5)
Displays a detailed breakdown of how your merit score was calculated.

---

## 7. Admin Panel Guide <a name="admin"></a>

Access via **Main Menu → Option 2**.

Login: `admin` / `admin123`

### Dashboard
Shown at the top of every admin screen — displays live counts of Pending, Approved, Rejected, and Waitlisted applications.

### Application Management

| Option | Action |
|--------|--------|
| 1 | View all applications (all statuses) |
| 2 | View only pending applications |
| 3 | **Approve** — checks merit ≥ cut-off, decrements seat count |
| 4 | **Reject** — enter a reason displayed to student |
| 5 | **Waitlist** — holds application pending seat availability |
| 6 | **Auto Bulk Approve** — processes all pending apps automatically |
| 7 | **Verify Documents** — mark/unmark document verification |

> **Auto Bulk Approve** logic:
> - Merit ≥ cut-off AND seats available → **Approved**
> - Merit < cut-off → **Rejected** with auto-reason

### Student Management

| Option | Action |
|--------|--------|
| 8 | View all students sorted by merit (highest first) |
| 9 | Search by name or email keyword |
| 10 | View merit list for a given cut-off threshold |

### Course Management

| Option | Action |
|--------|--------|
| 11 | View all courses with seat availability |
| 12 | Add a new course |
| 13 | Update the merit cut-off for an existing course |

### Reports

| Option | Output |
|--------|--------|
| 14 | Approved admission list → CSV |
| 15 | Approved admission list → formatted TXT report |
| 16 | Merit list for a cut-off → CSV |
| 17 | All applications (all statuses) → CSV |

All reports are saved to the `output/` folder with a timestamp in the filename.

---

## 8. Generated Reports <a name="reports"></a>

### Admission List CSV
```
Application ID, Student Name, Email, Merit Score, Course Code, Course Name,
Cut-Off, Status, Reviewed By, Reviewed At, Documents Verified
```

### Admission Report TXT
A formatted, printable text report grouped by course with rank, student name, merit score and document status.

### Merit List CSV
```
Rank, Student ID, Full Name, Email, 10th %, 12th %, Entrance Score, Merit Score
```

Sample output files are included in `output/` for reference.

---

## 9. Project Structure <a name="structure"></a>

```
CollegeAdmissionSystem/
├── pom.xml                          ← Maven build config
├── resources/
│   ├── schema.sql                   ← Database DDL + seed data
│   └── db.properties                ← DB connection config
├── output/                          ← Generated reports land here
│   ├── sample_admission_list.csv
│   └── sample_merit_report.txt
└── src/main/java/com/admission/
    ├── Main.java                    ← Application entry point
    ├── model/
    │   ├── Student.java
    │   ├── Course.java
    │   └── Application.java
    ├── dao/
    │   ├── StudentDAO.java          ← JDBC CRUD for Students
    │   ├── CourseDAO.java           ← JDBC CRUD for Courses
    │   └── ApplicationDAO.java     ← JDBC CRUD for Applications
    ├── service/
    │   └── AdmissionService.java   ← Business logic layer
    ├── ui/
    │   ├── StudentConsoleUI.java   ← Student portal menus
    │   └── AdminConsoleUI.java     ← Admin panel menus
    └── util/
        ├── DBConnection.java       ← JDBC connection manager
        └── ReportExporter.java     ← CSV + TXT report generator
```

---

## 10. Troubleshooting <a name="troubleshooting"></a>

| Problem | Solution |
|---------|----------|
| `Connection FAILED` on start | Check MySQL is running; verify `db.properties` credentials |
| `Duplicate entry for email` | That email is already registered; use a different one |
| `No seats available` | Course is full; try another course |
| `Merit below cut-off` | Student's merit score doesn't meet the course requirement |
| `java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Rebuild with `mvn clean package`; ensure fat JAR is used |
| Build fails | Ensure Java 17+ is installed: `java -version` |

---

*College Admission Management System v1.0 — Built with Java 17 + JDBC + MySQL*
