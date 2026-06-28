package com.admission.ui;

import com.admission.model.Application;
import com.admission.model.Course;
import com.admission.model.Student;
import com.admission.service.AdmissionService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Student-facing console menu: registration, view courses, apply.
 */
public class StudentConsoleUI {

    private final AdmissionService service;
    private final Scanner sc;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StudentConsoleUI(AdmissionService service, Scanner sc) {
        this.service = service;
        this.sc = sc;
    }

    public void show() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║      STUDENT PORTAL              ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║ 1. Register New Student          ║");
            System.out.println("║ 2. View Available Courses        ║");
            System.out.println("║ 3. Apply for a Course            ║");
            System.out.println("║ 4. Check My Application Status   ║");
            System.out.println("║ 5. View My Merit Score           ║");
            System.out.println("║ 0. Back to Main Menu             ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Select: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> registerStudent();
                case "2" -> viewCourses();
                case "3" -> applyForCourse();
                case "4" -> checkApplicationStatus();
                case "5" -> viewMeritScore();
                case "0" -> { return; }
                default -> System.out.println("⚠ Invalid option.");
            }
        }
    }

    // ---- REGISTER ----
    private void registerStudent() {
        System.out.println("\n--- STUDENT REGISTRATION ---");
        try {
            System.out.print("First Name       : "); String fn = sc.nextLine().trim();
            System.out.print("Last Name        : "); String ln = sc.nextLine().trim();
            System.out.print("Email            : "); String em = sc.nextLine().trim();
            System.out.print("Phone            : "); String ph = sc.nextLine().trim();
            System.out.print("Date of Birth (yyyy-MM-dd): "); String dobStr = sc.nextLine().trim();
            System.out.print("Gender (Male/Female/Other): "); String gen = sc.nextLine().trim();
            System.out.print("Address          : "); String addr = sc.nextLine().trim();
            System.out.print("City             : "); String city = sc.nextLine().trim();
            System.out.print("State            : "); String state = sc.nextLine().trim();
            System.out.print("Pincode          : "); String pin = sc.nextLine().trim();
            System.out.print("10th Marks (%)   : "); double tenth = Double.parseDouble(sc.nextLine().trim());
            System.out.print("12th Marks (%)   : "); double twelfth = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Entrance Score   : "); double entrance = Double.parseDouble(sc.nextLine().trim());

            LocalDate dob = LocalDate.parse(dobStr, DATE_FMT);
            Student s = new Student(fn, ln, em, ph, dob, gen, addr, city, state, pin, tenth, twelfth, entrance);
            int id = service.registerStudent(s);
            double merit = Student.calculateMerit(tenth, twelfth, entrance);
            System.out.printf("%n✅ Registration successful! Student ID: %d | Merit Score: %.3f%n", id, merit);
            System.out.println("   Formula: (10th×0.20) + (12th×0.50) + (Entrance×0.30)");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number input: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---- VIEW COURSES ----
    private void viewCourses() {
        try {
            List<Course> courses = service.getAllCourses();
            System.out.println("\n--- AVAILABLE COURSES ---");
            System.out.printf("%-4s %-8s %-40s %-20s %5s %8s %10s%n",
                    "ID","Code","Course Name","Department","Seats","Cut-Off","Fee/Year");
            System.out.println("-".repeat(100));
            for (Course c : courses) {
                System.out.printf("%-4d %-8s %-40s %-20s %5d %8.2f %10.2f%n",
                        c.getCourseId(), c.getCourseCode(), c.getCourseName(),
                        c.getDepartment(), c.getAvailableSeats(), c.getCutOffMerit(), c.getFeePerYear());
            }
        } catch (SQLException e) {
            System.out.println("❌ DB error: " + e.getMessage());
        }
    }

    // ---- APPLY ----
    private void applyForCourse() {
        System.out.println("\n--- APPLY FOR COURSE ---");
        try {
            System.out.print("Enter your Student ID : "); int sid = Integer.parseInt(sc.nextLine().trim());
            Student s = service.getStudent(sid);
            if (s == null) { System.out.println("❌ Student not found."); return; }
            System.out.printf("Welcome, %s (Merit: %.3f)%n", s.getFullName(), s.getMeritScore());
            viewCourses();
            System.out.print("Enter Course ID to apply: "); int cid = Integer.parseInt(sc.nextLine().trim());
            int appId = service.applyForCourse(sid, cid);
            System.out.printf("✅ Application submitted! Application ID: %d — Status: Pending%n", appId);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---- CHECK STATUS ----
    private void checkApplicationStatus() {
        System.out.print("\nEnter your Student ID: ");
        try {
            int sid = Integer.parseInt(sc.nextLine().trim());
            List<Application> apps = service.getApplicationsByStudent(sid);
            if (apps.isEmpty()) { System.out.println("No applications found."); return; }
            System.out.println("\n--- YOUR APPLICATIONS ---");
            System.out.printf("%-6s %-35s %-12s %-8s %-40s%n",
                    "AppID","Course","Status","Merit","Remarks");
            System.out.println("-".repeat(105));
            for (Application a : apps) {
                System.out.printf("%-6d %-35s %-12s %-8.3f %-40s%n",
                        a.getApplicationId(),
                        a.getCourseName() != null ? a.getCourseName() : "N/A",
                        a.getStatus(),
                        a.getMeritScore(),
                        a.getAdminRemarks() != null ? a.getAdminRemarks() : "-");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ---- MERIT SCORE ----
    private void viewMeritScore() {
        System.out.print("\nEnter your Student ID: ");
        try {
            int sid = Integer.parseInt(sc.nextLine().trim());
            Student s = service.getStudent(sid);
            if (s == null) { System.out.println("❌ Student not found."); return; }
            System.out.println("\n--- MERIT DETAILS ---");
            System.out.printf("Name          : %s%n", s.getFullName());
            System.out.printf("10th Marks    : %.2f%%%n", s.getTenthMarks());
            System.out.printf("12th Marks    : %.2f%%%n", s.getTwelfthMarks());
            System.out.printf("Entrance Score: %.2f%n", s.getEntranceScore());
            System.out.printf("Merit Score   : %.3f%n", s.getMeritScore());
            System.out.println("Formula: (10th×0.20) + (12th×0.50) + (Entrance×0.30)");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
