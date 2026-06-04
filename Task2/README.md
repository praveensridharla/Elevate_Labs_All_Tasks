# Student Management System (Task 2)

This is a simple console-based Student Management System implemented in Java as part of Task 2.

**Description:**
- A small CRUD application to add, view, update, and delete student records.

**Features:**
- Add a student with `ID`, `Name`, and `Marks`.
- View a student by `ID`.
- View all students in a table format.
- Update a student's name and marks.
- Delete a student by `ID`.

**Files:**
- [StudentManagementSystem.java](StudentManagementSystem.java#L1-L400) — main application and menu logic.
- [Student.java](Student.java#L1-L200) — `Student` model class with getters, setters, and `toString()`.

**Requirements:**
- Java 8 or later (tested with Java 17).

**Build & Run:**
Compile the sources and run the application from the project directory:

```bash
javac Student.java StudentManagementSystem.java
java StudentManagementSystem
```

**Usage:**
- Follow the on-screen menu to choose options (Add, View, Update, Delete, Exit).
- Input is validated for integers and marks are restricted to the 0–100 range.

**Notes:**
- The program stores data in memory only (no persistence). Exiting the program will lose all records.
- This implementation focuses on clear input validation and user-friendly messages.

If you want, I can add example screenshots, sample data, or persist records to a file—tell me which.


**OUTPUTS**

![alt text](<Screenshot 2026-06-01 210805.png>)
![alt text](<Screenshot 2026-06-01 210834.png>)
![alt text](<Screenshot 2026-06-01 210847.png>)
