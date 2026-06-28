package com.admission;

import com.admission.service.AdmissionService;
import com.admission.ui.AdminConsoleUI;
import com.admission.ui.StudentConsoleUI;
import com.admission.util.DBConnection;

import java.io.File;
import java.util.Scanner;

/**
 * Entry point for the College Admission Management System.
 * Runs a console-based menu with Student and Admin portals.
 */
public class Main {

    private static final String OUTPUT_DIR = "output";

    public static void main(String[] args) {
        // Ensure output directory exists
        new File(OUTPUT_DIR).mkdirs();

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║    COLLEGE ADMISSION MANAGEMENT SYSTEM           ║");
        System.out.println("║    Powered by Java + JDBC + MySQL                ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // Test DB connection
        System.out.print("\nConnecting to database... ");
        if (!DBConnection.testConnection()) {
            System.out.println("FAILED ❌");
            System.out.println("Please check your MySQL server and db.properties configuration.");
            System.out.println("Run resources/schema.sql first to set up the database.");
            System.exit(1);
        }
        System.out.println("OK ✅");

        AdmissionService service = new AdmissionService();
        Scanner sc = new Scanner(System.in);

        StudentConsoleUI studentUI = new StudentConsoleUI(service, sc);
        AdminConsoleUI   adminUI   = new AdminConsoleUI(service, sc, OUTPUT_DIR);

        while (true) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║           MAIN MENU                      ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Student Portal                       ║");
            System.out.println("║  2. Admin Panel (login required)         ║");
            System.out.println("║  0. Exit                                 ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Select: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> studentUI.show();
                case "2" -> { if (adminUI.login()) adminUI.show(); }
                case "0" -> {
                    System.out.println("Goodbye! 👋");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("⚠ Invalid option.");
            }
        }
    }
}
