import java.io.*;

/**
 * ╔═══════════════════════════════════════════════════════╗
 *   FileIOConcepts.java  –  Reference & Learning Guide
 *   Illustrates every File I/O concept used in NotesApp
 * ╚═══════════════════════════════════════════════════════╝
 */
public class FileIOConcepts {

    public static void main(String[] args) throws IOException {

        // ─────────────────────────────────────────────────
        // CONCEPT 1: FileWriter  (character-based writing)
        // ─────────────────────────────────────────────────
        // FileWriter(path)        → OVERWRITES the file
        // FileWriter(path, true)  → APPENDS to the file
        System.out.println("── CONCEPT 1: FileWriter ──");
        try (FileWriter fw = new FileWriter("demo_output.txt")) {
            fw.write("Line 1: Hello from FileWriter\n");
            fw.write("Line 2: Second line\n");
        } // auto-closes (try-with-resources)

        // Append mode
        try (FileWriter fw = new FileWriter("demo_output.txt", true)) {
            fw.write("Line 3: Appended line\n");
        }

        // ─────────────────────────────────────────────────
        // CONCEPT 2: BufferedWriter  (faster, line helpers)
        // ─────────────────────────────────────────────────
        System.out.println("── CONCEPT 2: BufferedWriter ──");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("demo_buffered.txt"))) {
            bw.write("BufferedWriter is faster because it batches writes.");
            bw.newLine();                       // OS-independent newline (\n or \r\n)
            bw.write("Always wrap FileWriter with BufferedWriter for performance.");
            bw.newLine();
            bw.flush();                         // optional when using try-with-resources
        }

        // ─────────────────────────────────────────────────
        // CONCEPT 3: FileReader  (character-based reading)
        // ─────────────────────────────────────────────────
        System.out.println("── CONCEPT 3: FileReader ──");
        try (FileReader fr = new FileReader("demo_output.txt")) {
            int ch;
            System.out.print("Raw char read: ");
            while ((ch = fr.read()) != -1) {    // -1 signals end of file
                System.out.print((char) ch);    // cast int → char
            }
        }

        // ─────────────────────────────────────────────────
        // CONCEPT 4: BufferedReader  (line-by-line reading)
        // ─────────────────────────────────────────────────
        System.out.println("── CONCEPT 4: BufferedReader ──");
        try (BufferedReader br = new BufferedReader(new FileReader("demo_buffered.txt"))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {  // null = end of file
                System.out.printf("[%d] %s%n", lineNum++, line);
            }
        }

        // ─────────────────────────────────────────────────
        // CONCEPT 5: File class  (metadata & management)
        // ─────────────────────────────────────────────────
        System.out.println("── CONCEPT 5: File class ──");
        File f = new File("demo_output.txt");
        System.out.println("Exists   : " + f.exists());
        System.out.println("Path     : " + f.getAbsolutePath());
        System.out.println("Size     : " + f.length() + " bytes");
        System.out.println("Readable : " + f.canRead());
        System.out.println("Writable : " + f.canWrite());

        // ─────────────────────────────────────────────────
        // CONCEPT 6: Try-with-resources  (auto close)
        // ─────────────────────────────────────────────────
        // Any class implementing AutoCloseable can be used.
        // FileWriter, FileReader, BufferedWriter, BufferedReader all qualify.
        // Resources are closed in reverse order of declaration.
        System.out.println("── CONCEPT 6: Try-with-resources ──");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("demo_try.txt"));
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println("Both bw and pw closed automatically!");
        }

        // ─────────────────────────────────────────────────
        // CONCEPT 7: IOException handling
        // ─────────────────────────────────────────────────
        System.out.println("── CONCEPT 7: IOException ──");
        try (BufferedReader br = new BufferedReader(new FileReader("nonexistent.txt"))) {
            br.readLine();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }

        // ─────────────────────────────────────────────────
        // CONCEPT 8: PrintWriter  (printf-style writing)
        // ─────────────────────────────────────────────────
        System.out.println("── CONCEPT 8: PrintWriter ──");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("demo_print.txt")))) {
            pw.println("PrintWriter lets you use println/printf!");
            pw.printf("Formatted: Name=%-10s Age=%d%n", "Alice", 30);
        }

        // Cleanup demo files
        new File("demo_output.txt").delete();
        new File("demo_buffered.txt").delete();
        new File("demo_try.txt").delete();
        new File("demo_print.txt").delete();

        System.out.println("\n✅ All concepts demonstrated successfully!");
    }
}
