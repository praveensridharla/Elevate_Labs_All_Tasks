package com.admission.util;

import com.admission.model.Application;
import com.admission.model.Student;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates CSV and simple text-based PDF-style reports for admission lists.
 */
public class ReportExporter {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FILE_DT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // =====================================================================
    //  CSV EXPORT
    // =====================================================================

    /** Export full admission list (Approved applications) to CSV. */
    public static String exportAdmissionListCSV(List<Application> list, String outputDir) throws IOException {
        String filename = outputDir + File.separator + "admission_list_" + now() + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            // Header
            pw.println("Application ID,Student Name,Email,Merit Score,Course Code,Course Name,Cut-Off,Status,Reviewed By,Reviewed At,Documents Verified");
            for (Application a : list) {
                pw.printf("%d,\"%s\",\"%s\",%.3f,\"%s\",\"%s\",%.3f,%s,\"%s\",%s,%s%n",
                        a.getApplicationId(),
                        safe(a.getStudentName()),
                        safe(a.getStudentEmail()),
                        a.getMeritScore(),
                        safe(a.getCourseCode()),
                        safe(a.getCourseName()),
                        a.getCutOffMerit(),
                        a.getStatus(),
                        safe(a.getReviewedBy()),
                        a.getReviewedAt() != null ? a.getReviewedAt().format(DT) : "N/A",
                        a.isDocumentsVerified() ? "Yes" : "No"
                );
            }
        }
        return filename;
    }

    /** Export all applications (any status) to CSV. */
    public static String exportAllApplicationsCSV(List<Application> list, String outputDir) throws IOException {
        String filename = outputDir + File.separator + "all_applications_" + now() + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("Application ID,Student ID,Student Name,Email,Merit Score,Course Code,Course Name,Cut-Off,Status,Admin Remarks,Reviewed By,Application Date");
            for (Application a : list) {
                pw.printf("%d,%d,\"%s\",\"%s\",%.3f,\"%s\",\"%s\",%.3f,%s,\"%s\",\"%s\",%s%n",
                        a.getApplicationId(),
                        a.getStudentId(),
                        safe(a.getStudentName()),
                        safe(a.getStudentEmail()),
                        a.getMeritScore(),
                        safe(a.getCourseCode()),
                        safe(a.getCourseName()),
                        a.getCutOffMerit(),
                        a.getStatus(),
                        safe(a.getAdminRemarks()),
                        safe(a.getReviewedBy()),
                        a.getApplicationDate() != null ? a.getApplicationDate().format(DT) : "N/A"
                );
            }
        }
        return filename;
    }

    /** Export merit list (students) to CSV. */
    public static String exportMeritListCSV(List<Student> students, String outputDir) throws IOException {
        String filename = outputDir + File.separator + "merit_list_" + now() + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("Rank,Student ID,Full Name,Email,10th %,12th %,Entrance Score,Merit Score");
            int rank = 1;
            for (Student s : students) {
                pw.printf("%d,%d,\"%s\",\"%s\",%.2f,%.2f,%.2f,%.3f%n",
                        rank++,
                        s.getStudentId(),
                        s.getFullName(),
                        s.getEmail(),
                        s.getTenthMarks(),
                        s.getTwelfthMarks(),
                        s.getEntranceScore(),
                        s.getMeritScore()
                );
            }
        }
        return filename;
    }

    // =====================================================================
    //  TEXT/PDF-STYLE REPORT (plain text formatted as a printable report)
    // =====================================================================

    /** Generate a formatted text report resembling a PDF layout. */
    public static String exportAdmissionListTXT(List<Application> list, String outputDir) throws IOException {
        String filename = outputDir + File.separator + "admission_report_" + now() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            String line = "=".repeat(110);
            String thin = "-".repeat(110);

            pw.println(line);
            pw.println(center("COLLEGE ADMISSION MANAGEMENT SYSTEM", 110));
            pw.println(center("FINAL ADMISSION LIST", 110));
            pw.println(center("Generated: " + LocalDateTime.now().format(DT), 110));
            pw.println(line);
            pw.println();

            // Group by course
            String currentCourse = "";
            int rank = 1;
            for (Application a : list) {
                String course = a.getCourseCode() + " - " + a.getCourseName();
                if (!course.equals(currentCourse)) {
                    if (!currentCourse.isEmpty()) pw.println();
                    pw.println(thin);
                    pw.printf("  COURSE: %-50s  Cut-Off Merit: %.2f%n", course, a.getCutOffMerit());
                    pw.println(thin);
                    pw.printf("  %-5s %-8s %-30s %-30s %-10s %-6s%n",
                            "Rank", "App ID", "Student Name", "Email", "Merit", "Docs");
                    pw.println(thin);
                    currentCourse = course;
                    rank = 1;
                }
                pw.printf("  %-5d %-8d %-30s %-30s %-10.3f %-6s%n",
                        rank++,
                        a.getApplicationId(),
                        truncate(a.getStudentName(), 28),
                        truncate(a.getStudentEmail(), 28),
                        a.getMeritScore(),
                        a.isDocumentsVerified() ? "Yes" : "No"
                );
            }

            pw.println();
            pw.println(line);
            pw.printf("  Total Admissions: %d%n", list.size());
            pw.println(line);
        }
        return filename;
    }

    /** Generate a student merit list text report. */
    public static String exportMeritListTXT(List<Student> students, double cutOff, String outputDir) throws IOException {
        String filename = outputDir + File.separator + "merit_report_" + now() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            String line = "=".repeat(100);
            String thin = "-".repeat(100);

            pw.println(line);
            pw.println(center("COLLEGE ADMISSION MANAGEMENT SYSTEM", 100));
            pw.println(center("MERIT LIST REPORT", 100));
            pw.println(center("Cut-Off: " + cutOff + " | Generated: " + LocalDateTime.now().format(DT), 100));
            pw.println(line);
            pw.printf("  %-5s %-8s %-30s %-28s %8s %8s %9s %10s%n",
                    "Rank","ID","Full Name","Email","10th%","12th%","Entrance","Merit");
            pw.println(thin);

            int rank = 1;
            for (Student s : students) {
                pw.printf("  %-5d %-8d %-30s %-28s %8.2f %8.2f %9.2f %10.3f%n",
                        rank++,
                        s.getStudentId(),
                        truncate(s.getFullName(), 28),
                        truncate(s.getEmail(), 26),
                        s.getTenthMarks(),
                        s.getTwelfthMarks(),
                        s.getEntranceScore(),
                        s.getMeritScore()
                );
            }
            pw.println(thin);
            pw.printf("  Total Eligible Students: %d%n", students.size());
            pw.println(line);
        }
        return filename;
    }

    // =====================================================================
    //  HELPERS
    // =====================================================================

    private static String now() { return LocalDateTime.now().format(FILE_DT); }

    private static String safe(String s) { return s == null ? "" : s.replace("\"", "'"); }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String center(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + text;
    }
}
