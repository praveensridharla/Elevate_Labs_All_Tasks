// Main.java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        String choice;

        // Sample Data
        library.addBook(new Book("B001", "The Alchemist", "Paulo Coelho"));
        library.addBook(new Book("B002", "Atomic Habits", "James Clear"));
        library.addBook(new Book("B003", "Rich Dad Poor Dad", "Robert Kiyosaki"));

        library.addUser(new User("U001", "Praveen"));
        library.addUser(new User("U002", "Rahul Sharma"));

        System.out.println("=====================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=====================================");

        do {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Add New Book");
            System.out.println("2. Add New User");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. View All Books");
            System.out.println("6. View Available Books");
            System.out.println("7. View All Users");
            System.out.println("8. View User's Borrowed Books");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Book ID: ");
                    String bid = scanner.nextLine();
                    System.out.print("Enter Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Author: ");
                    String author = scanner.nextLine();
                    library.addBook(new Book(bid, title, author));
                    break;

                case "2":
                    System.out.print("Enter User ID: ");
                    String uid = scanner.nextLine();
                    System.out.print("Enter User Name: ");
                    String name = scanner.nextLine();
                    library.addUser(new User(uid, name));
                    break;

                case "3":
                    System.out.print("Enter Book ID: ");
                    String issueBid = scanner.nextLine();
                    System.out.print("Enter User ID: ");
                    String issueUid = scanner.nextLine();
                    library.issueBook(issueBid, issueUid);
                    break;

                case "4":
                    System.out.print("Enter Book ID: ");
                    String returnBid = scanner.nextLine();
                    System.out.print("Enter User ID: ");
                    String returnUid = scanner.nextLine();
                    library.returnBook(returnBid, returnUid);
                    break;

                case "5":
                    library.displayAllBooks();
                    break;

                case "6":
                    library.displayAvailableBooks();
                    break;

                case "7":
                    library.displayUsers();
                    break;

                case "8":
                    System.out.print("Enter User ID: ");
                    String viewUid = scanner.nextLine();
                    library.displayUserBooks(viewUid);
                    break;

                case "9":
                    System.out.println("Thank you for using Library Management System!");
                    break;

                default:
                    System.out.println("❌ Invalid choice! Please try again.");
            }
        } while (!choice.equals("9"));

        scanner.close();
    }
}