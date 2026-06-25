package com.admission.ui;

import com.admission.model.Application;
import com.admission.model.Application.Status;
import com.admission.model.Course;
import com.admission.model.Student;
import com.admission.service.AdmissionService;
import com.admission.util.ReportExporter;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Admin console panel: approve/reject applications, manage courses,
 * view merit lists and generate reports.
 */
public class AdminConsoleUI {

    private final AdmissionService service;
    private final Scanner sc;
    private String adminName = "Admin";
    private final String outputDir;

    public AdminConsoleUI(AdmissionService service, Scanner sc, String outputDir) {
        this.service   = service;
        this.sc        = sc;
        this.outputDir = outputDir;
    }

    // ---- Simple credential check ----
    public boolean login() {
        System.out.println("\n--- ADMIN LOGIN ---");
        System.out.print("Username: "); String u = sc.nextLine().trim();
        System.out.print("Password: "); String p = sc.nextLine().trim();
        // Default credentials (in prod, verify against DB hash)
        if ("admin".equals(u) && "admin123".equals(p)) {
            adminName = u;
            System.out.println("✅ Login successful. Welcome, " + adminName);
            return true;
        }
        System.out.println("❌ Invalid credentials.");
        return false;
    }

    public void show() {
        while (true) {
            printDashboard();
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║         ADMIN PANEL                  ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ APPLICATION MANAGEMENT               ║");
            System.out.println("║  1. View All Applications            ║");
            System.out.println("║  2. View Pending Applications        ║");
            System.out.println("║  3. Approve Application              ║");
            System.out.println("║  4. Reject  Application              ║");
            System.out.println("║  5. Waitlist Application             ║");
            System.out.println("║  6. Auto Bulk Approve (Merit-Based)  ║");
            System.out.println("║  7. Verify Documents                 ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ STUDENT MANAGEMENT                   ║");
            System.out.println("║  8. View All Students                ║");
            System.out.println("║  9. Search Students                  ║");
            System.out.println("║ 10. View Merit List                  ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ COURSE MANAGEMENT                    ║");
            System.out.println("║ 11. View All Courses                 ║");
            System.out.println("║ 12. Add New Course                   ║");
            System.out.println("║ 13. Update Cut-Off for Course        ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ REPORTS                              ║");
            System.out.println("║ 14. Export Admission List (CSV)      ║");
            System.out.println("║ 15. Export Admission Report (TXT)    ║");
            System.out.println("║ 16. Export Merit List (CSV)          ║");
            System.out.println("║ 17. Export All Applications (CSV)    ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  0. Logout                           ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Select: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1"  -> viewAllApplications();
                case "2"  -> viewPendingApplications();
                case "3"  -> approveApplication();
                case "4"  -> rejectApplication();
                case "5"  -> waitlistApplication();
                case "6"  -> autoBulkApprove();
                case "7"  -> verifyDocuments();
                case "8"  -> viewAllStudents();
                case "9"  -> searchStudents();
                case "10" -> viewMeritList();
                case "11" -> viewAllCourses();
                case "12" -> addCourse();
                case "13" -> updateCutOff();
                case "14" -> exportAdmissionCSV();
                case "15" -> exportAdmissionTXT();
                case "16" -> exportMeritCSV();
                case "17" -> exportAllApplicationsCSV();
                case "0"  -> { System.out.println("Logged out."); return; }
                default   -> System.out.println("⚠ Invalid option.");
            }
        }
    }

    // ---- DASHBOARD ----
    private void printDashboard() {
        try {
            int[] stats = service.getDashboardStats();
            System.out.println("\n┌─ DASHBOARD ───────────────────────────────┐");
            System.out.printf("│  Pending: %-5d  Approved: %-5d            │%n", stats[0], stats[1]);
            System.out.printf("│  Rejected: %-4d  Waitlisted: %-4d          │%n", stats[2], stats[3]);
            System.out.println("└────────────────────────────────────────────┘");
        } catch (SQLException e) {
            System.out.println("(Dashboard unavailable)");
        }
    }

    // ---- VIEW ALL APPLICATIONS ----
    private void viewAllApplications() {
        try {
            List<Application> apps = service.getAllApplications();
            printApplicationTable("ALL APPLICATIONS", apps);
        } catch (SQLException e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- VIEW PENDING ----
    private void viewPendingApplications() {
        try {
            List<Application> apps = service.getPendingApplications();
            printApplicationTable("PENDING APPLICATIONS", apps);
        } catch (SQLException e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- APPROVE ----
    private void approveApplication() {
        System.out.print("\nEnter Application ID to approve: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Remarks (or press Enter): ");
            String remarks = sc.nextLine().trim();
            if (remarks.isEmpty()) remarks = "Approved by admin";
            service.approveApplication(id, adminName, remarks);
            System.out.println("✅ Application #" + id + " APPROVED.");
        } catch (IllegalStateException e) {
            System.out.println("❌ Cannot approve: " + e.getMessage());
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- REJECT ----
    private void rejectApplication() {
        System.out.print("\nEnter Application ID to reject: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Reason for rejection: ");
            String remarks = sc.nextLine().trim();
            service.rejectApplication(id, adminName, remarks);
            System.out.println("✅ Application #" + id + " REJECTED.");
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- WAITLIST ----
    private void waitlistApplication() {
        System.out.print("\nEnter Application ID to waitlist: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Remarks: ");
            String remarks = sc.nextLine().trim();
            service.waitlistApplication(id, adminName, remarks);
            System.out.println("✅ Application #" + id + " WAITLISTED.");
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- BULK APPROVE ----
    private void autoBulkApprove() {
        System.out.print("\n⚠  This will auto-approve all Pending apps meeting merit cut-off.\nConfirm? (yes/no): ");
        if (!"yes".equalsIgnoreCase(sc.nextLine().trim())) { System.out.println("Cancelled."); return; }
        try {
            int count = service.autoBulkApprove(adminName);
            System.out.println("✅ Bulk process complete. Approved: " + count + " applications.");
        } catch (SQLException e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- VERIFY DOCUMENTS ----
    private void verifyDocuments() {
        System.out.print("\nEnter Application ID: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Mark documents as verified? (yes/no): ");
            boolean v = "yes".equalsIgnoreCase(sc.nextLine().trim());
            service.verifyDocuments(id, v);
            System.out.println("✅ Documents marked as " + (v ? "VERIFIED" : "UNVERIFIED") + ".");
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- VIEW ALL STUDENTS ----
    private void viewAllStudents() {
        try {
            List<Student> students = service.getAllStudents();
            System.out.println("\n--- ALL STUDENTS (sorted by merit) ---");
            System.out.printf("%-5s %-25s %-30s %8s %8s %9s %10s%n",
                    "ID","Name","Email","10th%","12th%","Entrance","Merit");
            System.out.println("-".repeat(100));
            for (Student s : students) {
                System.out.printf("%-5d %-25s %-30s %8.2f %8.2f %9.2f %10.3f%n",
                        s.getStudentId(), s.getFullName(), s.getEmail(),
                        s.getTenthMarks(), s.getTwelfthMarks(),
                        s.getEntranceScore(), s.getMeritScore());
            }
            System.out.println("Total: " + students.size());
        } catch (SQLException e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- SEARCH STUDENTS ----
    private void searchStudents() {
        System.out.print("\nSearch keyword (name/email): ");
        String kw = sc.nextLine().trim();
        try {
            List<Student> students = service.searchStudents(kw);
            if (students.isEmpty()) { System.out.println("No results found."); return; }
            System.out.println("\n--- SEARCH RESULTS ---");
            for (Student s : students)
                System.out.printf("[%d] %s | %s | Merit: %.3f%n",
                        s.getStudentId(), s.getFullName(), s.getEmail(), s.getMeritScore());
        } catch (SQLException e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- MERIT LIST ----
    private void viewMeritList() {
        System.out.print("\nEnter minimum cut-off merit (e.g. 65.0): ");
        try {
            double cutOff = Double.parseDouble(sc.nextLine().trim());
            List<Student> students = service.getMeritList(cutOff);
            System.out.printf("%n--- MERIT LIST (≥ %.2f) ---%n", cutOff);
            int rank = 1;
            for (Student s : students)
                System.out.printf("Rank %-3d | [%d] %-25s | Merit: %.3f%n",
                        rank++, s.getStudentId(), s.getFullName(), s.getMeritScore());
            System.out.println("Total eligible: " + students.size());
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- VIEW ALL COURSES ----
    private void viewAllCourses() {
        try {
            List<Course> courses = service.getAllCourses();
            System.out.println("\n--- COURSES ---");
            System.out.printf("%-4s %-8s %-40s %8s %6s %8s%n",
                    "ID","Code","Course","Cut-Off","Seats","Fee/Yr");
            System.out.println("-".repeat(80));
            for (Course c : courses)
                System.out.printf("%-4d %-8s %-40s %8.2f %6d %8.0f%n",
                        c.getCourseId(), c.getCourseCode(), c.getCourseName(),
                        c.getCutOffMerit(), c.getAvailableSeats(), c.getFeePerYear());
        } catch (SQLException e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- ADD COURSE ----
    private void addCourse() {
        System.out.println("\n--- ADD NEW COURSE ---");
        try {
            System.out.print("Course Code   : "); String code = sc.nextLine().trim();
            System.out.print("Course Name   : "); String name = sc.nextLine().trim();
            System.out.print("Department    : "); String dept = sc.nextLine().trim();
            System.out.print("Duration (yrs): "); int dur = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Total Seats   : "); int seats = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Cut-Off Merit : "); double cutOff = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Fee / Year    : "); double fee = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Description   : "); String desc = sc.nextLine().trim();
            Course c = new Course(code, name, dept, dur, seats, cutOff, fee, desc);
            int id = service.addCourse(c);
            System.out.println("✅ Course added with ID: " + id);
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- UPDATE CUT-OFF ----
    private void updateCutOff() {
        System.out.print("\nEnter Course ID: ");
        try {
            int cid = Integer.parseInt(sc.nextLine().trim());
            Course c = service.getCourse(cid);
            if (c == null) { System.out.println("❌ Course not found."); return; }
            System.out.printf("Current cut-off for %s: %.2f%n", c.getCourseName(), c.getCutOffMerit());
            System.out.print("New cut-off merit: ");
            double co = Double.parseDouble(sc.nextLine().trim());
            c.setCutOffMerit(co);
            service.updateCourse(c);
            System.out.println("✅ Cut-off updated to " + co);
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- REPORTS ----
    private void exportAdmissionCSV() {
        try {
            List<Application> list = service.getAdmissionList();
            if (list.isEmpty()) { System.out.println("No approved applications to export."); return; }
            String path = ReportExporter.exportAdmissionListCSV(list, outputDir);
            System.out.println("✅ CSV saved: " + path);
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private void exportAdmissionTXT() {
        try {
            List<Application> list = service.getAdmissionList();
            if (list.isEmpty()) { System.out.println("No approved applications to export."); return; }
            String path = ReportExporter.exportAdmissionListTXT(list, outputDir);
            System.out.println("✅ Report saved: " + path);
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private void exportMeritCSV() {
        System.out.print("\nMinimum cut-off for merit list: ");
        try {
            double co = Double.parseDouble(sc.nextLine().trim());
            List<Student> students = service.getMeritList(co);
            String path = ReportExporter.exportMeritListCSV(students, outputDir);
            System.out.println("✅ Merit list CSV saved: " + path);
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private void exportAllApplicationsCSV() {
        try {
            List<Application> list = service.getAllApplications();
            String path = ReportExporter.exportAllApplicationsCSV(list, outputDir);
            System.out.println("✅ All applications CSV saved: " + path);
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ---- TABLE HELPER ----
    private void printApplicationTable(String title, List<Application> apps) {
        System.out.println("\n--- " + title + " (" + apps.size() + " records) ---");
        System.out.printf("%-6s %-25s %-25s %8s %-12s %-10s %-30s%n",
                "AppID","Student","Course","Merit","Status","Cut-Off","Remarks");
        System.out.println("-".repeat(120));
        for (Application a : apps) {
            System.out.printf("%-6d %-25s %-25s %8.3f %-12s %-10.2f %-30s%n",
                    a.getApplicationId(),
                    a.getStudentName() != null ? a.getStudentName() : "N/A",
                    a.getCourseName()  != null ? a.getCourseName()  : "N/A",
                    a.getMeritScore(),
                    a.getStatus(),
                    a.getCutOffMerit(),
                    a.getAdminRemarks() != null ? a.getAdminRemarks() : "-");
        }
    }
}
