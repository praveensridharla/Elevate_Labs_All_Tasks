// Library.java
import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> books;
    private List<User> users;

    public Library() {
        books = new ArrayList<>();
        users = new ArrayList<>();
    }

    // Add Book
    public void addBook(Book book) {
        books.add(book);
        System.out.println("✅ Book added successfully!");
    }

    // Add User
    public void addUser(User user) {
        users.add(user);
        System.out.println("✅ User added successfully!");
    }

    // Issue Book
    public void issueBook(String bookId, String userId) {
        Book book = findBookById(bookId);
        User user = findUserById(userId);

        if (book == null) {
            System.out.println("❌ Book not found!");
            return;
        }
        if (user == null) {
            System.out.println("❌ User not found!");
            return;
        }
        if (!book.isAvailable()) {
            System.out.println("❌ Book is already issued!");
            return;
        }

        book.setAvailable(false);
        user.borrowBook(book);
        System.out.println("✅ Book issued successfully to " + user.getName());
    }

    // Return Book
    public void returnBook(String bookId, String userId) {
        Book book = findBookById(bookId);
        User user = findUserById(userId);

        if (book == null || user == null) {
            System.out.println("❌ Invalid Book ID or User ID!");
            return;
        }

        if (book.isAvailable()) {
            System.out.println("❌ Book is not issued!");
            return;
        }

        book.setAvailable(true);
        user.returnBook(book);
        System.out.println("✅ Book returned successfully!");
    }

    // Helper methods
    private Book findBookById(String bookId) {
        for (Book b : books) {
            if (b.getBookId().equals(bookId)) {
                return b;
            }
        }
        return null;
    }

    private User findUserById(String userId) {
        for (User u : users) {
            if (u.getUserId().equals(userId)) {
                return u;
            }
        }
        return null;
    }

    // Display methods
    public void displayAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        System.out.println("\n=== All Books ===");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void displayAvailableBooks() {
        System.out.println("\n=== Available Books ===");
        boolean found = false;
        for (Book book : books) {
            if (book.isAvailable()) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) System.out.println("No available books.");
    }

    public void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        System.out.println("\n=== All Users ===");
        for (User user : users) {
            System.out.println(user);
        }
    }

    public void displayUserBooks(String userId) {
        User user = findUserById(userId);
        if (user == null) {
            System.out.println("❌ User not found!");
            return;
        }
        System.out.println("\n=== Books borrowed by " + user.getName() + " ===");
        if (user.getBorrowedBooks().isEmpty()) {
            System.out.println("No books borrowed.");
        } else {
            for (Book book : user.getBorrowedBooks()) {
                System.out.println(book);
            }
        }
    }
}