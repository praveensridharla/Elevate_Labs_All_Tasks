import java.util.*;

public class StudentManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("   Student Management System");
        System.out.println("=====================================");

        while (true) {
            displayMenu();
            int choice = getValidIntegerInput("Enter your choice: ");

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewStudent();
                case 3 -> viewAllStudents();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 6 -> {
                    System.out.println("Thank you for using Student Management System. Goodbye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("❌ Invalid option! Please try again.");
            }
            System.out.println();
        }
    }

    private static void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Add Student");
        System.out.println("2. View Student by ID");
        System.out.println("3. View All Students");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
    }

    private static void addStudent() {
        System.out.println("\n--- Add New Student ---");

        int id = getValidIntegerInput("Enter Student ID: ");
        if (isIdExists(id)) {
            System.out.println("❌ Error: Student with ID " + id + " already exists!");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine().trim();

        int marks = getValidIntegerInput("Enter Marks (0-100): ", 0, 100);

        students.add(new Student(id, name, marks));
        System.out.println("✅ Student added successfully!");
    }

    private static void viewStudent() {
        int id = getValidIntegerInput("\nEnter Student ID to view: ");
        
        findStudentById(id).ifPresentOrElse(
            System.out::println,
            () -> System.out.println("❌ Student with ID " + id + " not found!")
        );
    }

    private static void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found!");
            return;
        }

        System.out.println("\n--- All Students ---");
        System.out.printf("%-6s %-25s %-8s%n", "ID", "Name", "Marks");
        System.out.println("--------------------------------------------------");

        for (Student s : students) {
            System.out.printf("%-6d %-25s %-8d%n", s.getId(), s.getName(), s.getMarks());
        }
    }

    private static void updateStudent() {
        int id = getValidIntegerInput("\nEnter Student ID to update: ");

        findStudentById(id).ifPresentOrElse(student -> {
            System.out.print("Enter New Name: ");
            String newName = scanner.nextLine().trim();
            if (!newName.isEmpty()) {
                student.setName(newName);
            }

            int newMarks = getValidIntegerInput("Enter New Marks (0-100): ", 0, 100);
            student.setMarks(newMarks);

            System.out.println("✅ Student updated successfully!");
        }, () -> System.out.println("❌ Student not found!"));
    }

    private static void deleteStudent() {
        int id = getValidIntegerInput("\nEnter Student ID to delete: ");

        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
                System.out.println("✅ Student deleted successfully!");
                return;
            }
        }
        System.out.println("❌ Student not found!");
    }

    // Helper Methods
    private static Optional<Student> findStudentById(int id) {
        return students.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    private static boolean isIdExists(int id) {
        return findStudentById(id).isPresent();
    }

    private static int getValidIntegerInput(String prompt) {
        return getValidIntegerInput(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static int getValidIntegerInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("❌ Value must be between " + min + " and " + max + "!");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input! Please enter a valid integer.");
                scanner.nextLine(); // consume invalid input
            }
        }
    }
}