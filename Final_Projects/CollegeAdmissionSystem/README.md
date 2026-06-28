# College Admission Management System

A Java console application for managing college admission operations, built with Java 17, JDBC, MySQL, and Maven.

## Overview

This system supports:
- Student registration and academic profile creation
- Automatic merit score calculation
- Course browsing and application submission
- Admin approval, rejection, waitlisting, and bulk processing
- CSV and text report export for admissions and merit lists

## Tech Stack

- Java 17
- Maven
- JDBC
- MySQL
- Console-based UI

## Prerequisites

- Java JDK 17+
- Maven 3.8+
- MySQL Server 8.0+

## Setup

1. Create the database schema:

```bash
mysql -u root -p < resources/schema.sql
```

2. Update `resources/db.properties` with your database connection settings:

```properties
db.url=jdbc:mysql://localhost:3306/college_admission?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=YOUR_PASSWORD
```

3. Build the project:

```bash
mvn clean package
```

4. Run the application:

```bash
java -jar target/CollegeAdmissionSystem.jar
```

## Default Admin Login

- Username: `admin`
- Password: `admin123`

## Usage

After starting the application, choose from the main menu:
- `Student Portal` — register, view courses, apply, and check status
- `Admin Panel` — login to manage applications, students, courses, and export reports

## Project Structure

- `src/main/java/com/admission/Main.java` — application entry point
- `src/main/java/com/admission/dao` — database access objects
- `src/main/java/com/admission/model` — data model classes
- `src/main/java/com/admission/service` — business logic
- `src/main/java/com/admission/ui` — console user interface
- `src/main/java/com/admission/util` — database connection and report export utilities
- `resources/` — database properties and SQL schema
- `docs/USER_GUIDE.md` — detailed user guide
- `output/` — generated report files

## Notes

- The application validates database connectivity at startup.
- Reports are written to the `output/` directory.
- `resources/schema.sql` creates the required tables and default admin user.

## License

This repository is provided as-is for educational and project demonstration purposes.
