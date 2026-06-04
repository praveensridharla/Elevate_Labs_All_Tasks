import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * ╔══════════════════════════════════════╗
 *   Java File I/O – Notes Manager App
 *   Uses FileWriter, FileReader,
 *   BufferedReader, BufferedWriter
 * ╚══════════════════════════════════════╝
 *
 * Learning Objectives:
 *  - Persist data using FileWriter / FileReader
 *  - Read lines with BufferedReader
 *  - Use append mode in FileWriter
 *  - Handle IOException properly
 */
public class NotesApp {

    // ── Config ──────────────────────────────────────
    static final String NOTES_DIR  = "notes";
    static final String INDEX_FILE = NOTES_DIR + "/index.txt";  // tracks all note filenames
    static final Scanner scanner   = new Scanner(System.in);

    // ── Entry point ─────────────────────────────────
    public static void main(String[] args) {
        ensureNotesDirectory();
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║   📓  Java Notes Manager App     ║");
        System.out.println("╚══════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> writeNote();
                case "2" -> listNotes();
                case "3" -> readNote();
                case "4" -> searchNotes();
                case "5" -> deleteNote();
                case "6" -> { System.out.println("\n👋 Goodbye! Your notes are saved.\n"); running = false; }
                default  -> System.out.println("⚠  Invalid option. Please choose 1–6.");
            }
        }
        scanner.close();
    }

    // ── Menu ────────────────────────────────────────
    static void printMenu() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│           MAIN MENU             │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│  1. ✏  Write a new note         │");
        System.out.println("│  2. 📋  List all notes          │");
        System.out.println("│  3. 📖  Read a note             │");
        System.out.println("│  4. 🔍  Search inside notes     │");
        System.out.println("│  5. 🗑  Delete a note           │");
        System.out.println("│  6. 🚪  Exit                    │");
        System.out.println("└─────────────────────────────────┘");
        System.out.print("Enter choice: ");
    }

    // ────────────────────────────────────────────────
    // 1. WRITE NOTE  →  demonstrates FileWriter
    // ────────────────────────────────────────────────
    static void writeNote() {
        System.out.print("\n📝 Enter note title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) { System.out.println("⚠  Title cannot be empty."); return; }

        System.out.println("📝 Enter note content (type END on a new line to finish):");
        StringBuilder content = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            content.append(line).append("\n");
        }

        // Safe filename: replace spaces/special chars
        String filename = title.replaceAll("[^a-zA-Z0-9_\\-]", "_") + ".txt";
        String filepath = NOTES_DIR + "/" + filename;
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // ── FileWriter Demo ──────────────────────────
        // FileWriter(path, false) → overwrite mode  (default)
        // FileWriter(path, true)  → append mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false))) {
            writer.write("TITLE     : " + title);
            writer.newLine();
            writer.write("CREATED   : " + timestamp);
            writer.newLine();
            writer.write("─".repeat(40));
            writer.newLine();
            writer.write(content.toString());
            System.out.println("✅ Note saved to: " + filepath);
        } catch (IOException e) {
            System.out.println("❌ Error writing note: " + e.getMessage());
            return;
        }

        // Register filename in the index (append mode)
        registerInIndex(filename, title);
    }

    // ────────────────────────────────────────────────
    // 2. LIST NOTES  →  demonstrates BufferedReader
    // ────────────────────────────────────────────────
    static void listNotes() {
        List<String[]> entries = readIndex();
        if (entries.isEmpty()) {
            System.out.println("\n📭 No notes found. Write your first note!");
            return;
        }
        System.out.println("\n📋 Your Notes:");
        System.out.println("─".repeat(50));
        for (int i = 0; i < entries.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, entries.get(i)[1]);  // [1] = title
        }
        System.out.println("─".repeat(50));
    }

    // ────────────────────────────────────────────────
    // 3. READ NOTE  →  demonstrates FileReader + BufferedReader
    // ────────────────────────────────────────────────
    static void readNote() {
        List<String[]> entries = readIndex();
        if (entries.isEmpty()) { System.out.println("\n📭 No notes yet."); return; }

        listNotes();
        System.out.print("Enter note number to read: ");
        int idx = parseChoice(entries.size());
        if (idx == -1) return;

        String filepath = NOTES_DIR + "/" + entries.get(idx)[0];

        // ── FileReader → BufferedReader Demo ─────────
        System.out.println("\n" + "═".repeat(50));
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading note: " + e.getMessage());
        }
        System.out.println("═".repeat(50));
    }

    // ────────────────────────────────────────────────
    // 4. SEARCH NOTES  →  scans every file with BufferedReader
    // ────────────────────────────────────────────────
    static void searchNotes() {
        List<String[]> entries = readIndex();
        if (entries.isEmpty()) { System.out.println("\n📭 No notes to search."); return; }

        System.out.print("\n🔍 Enter keyword to search: ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        if (keyword.isEmpty()) { System.out.println("⚠  Keyword cannot be empty."); return; }

        int found = 0;
        System.out.println("\nResults for \"" + keyword + "\":");
        System.out.println("─".repeat(50));

        for (String[] entry : entries) {
            String filepath = NOTES_DIR + "/" + entry[0];
            try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
                String line;
                int lineNum = 0;
                boolean matched = false;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    if (line.toLowerCase().contains(keyword)) {
                        if (!matched) {
                            System.out.println("\n📄 Note: " + entry[1]);
                            matched = true;
                            found++;
                        }
                        System.out.printf("   Line %2d: %s%n", lineNum, line.trim());
                    }
                }
            } catch (IOException e) {
                System.out.println("❌ Could not read: " + entry[0]);
            }
        }

        if (found == 0) System.out.println("  No matches found.");
        System.out.println("─".repeat(50));
    }

    // ────────────────────────────────────────────────
    // 5. DELETE NOTE  →  remove file + update index
    // ────────────────────────────────────────────────
    static void deleteNote() {
        List<String[]> entries = readIndex();
        if (entries.isEmpty()) { System.out.println("\n📭 No notes to delete."); return; }

        listNotes();
        System.out.print("Enter note number to delete: ");
        int idx = parseChoice(entries.size());
        if (idx == -1) return;

        String[] entry = entries.get(idx);
        File file = new File(NOTES_DIR + "/" + entry[0]);

        System.out.print("⚠  Delete \"" + entry[1] + "\"? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Cancelled.");
            return;
        }

        if (file.delete()) {
            entries.remove(idx);
            rewriteIndex(entries);
            System.out.println("🗑  Note deleted.");
        } else {
            System.out.println("❌ Could not delete file.");
        }
    }

    // ────────────────────────────────────────────────
    // HELPERS
    // ────────────────────────────────────────────────

    /** Create the notes/ directory if it doesn't exist */
    static void ensureNotesDirectory() {
        File dir = new File(NOTES_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    /**
     * Append "filename|title" line to index.txt
     * Demonstrates FileWriter in APPEND mode
     */
    static void registerInIndex(String filename, String title) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INDEX_FILE, true))) {
            writer.write(filename + "|" + title);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("⚠  Could not update index: " + e.getMessage());
        }
    }

    /**
     * Read all entries from index.txt
     * Returns list of [filename, title] pairs
     * Demonstrates BufferedReader line-by-line reading
     */
    static List<String[]> readIndex() {
        List<String[]> entries = new ArrayList<>();
        File indexFile = new File(INDEX_FILE);
        if (!indexFile.exists()) return entries;

        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    // Only include if actual note file still exists
                    if (new File(NOTES_DIR + "/" + parts[0]).exists()) {
                        entries.add(parts);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading index: " + e.getMessage());
        }
        return entries;
    }

    /**
     * Rewrite the entire index (used after deletion)
     * Demonstrates FileWriter in OVERWRITE mode
     */
    static void rewriteIndex(List<String[]> entries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INDEX_FILE, false))) {
            for (String[] entry : entries) {
                writer.write(entry[0] + "|" + entry[1]);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Error updating index: " + e.getMessage());
        }
    }

    /** Parse a menu number input safely */
    static int parseChoice(int max) {
        try {
            int n = Integer.parseInt(scanner.nextLine().trim());
            if (n < 1 || n > max) { System.out.println("⚠  Out of range."); return -1; }
            return n - 1;
        } catch (NumberFormatException e) {
            System.out.println("⚠  Please enter a valid number.");
            return -1;
        }
    }
}
