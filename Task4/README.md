# 📓 Java File I/O – Notes Manager App
### Task 4 · Java File I/O · VS Code + Terminal

---

## 📁 Project Structure
```
NotesApp/
├── src/
│   ├── NotesApp.java        ← Main app (run this)
│   └── FileIOConcepts.java  ← Learning reference / cheat sheet
├── notes/                   ← Auto-created; stores your note files
└── README.md
```

---

## 🚀 How to Run

### Option A — VS Code
1. Open the `NotesApp/` folder in VS Code
2. Install the **Extension Pack for Java** (if not already installed)
3. Right-click `src/NotesApp.java` → **Run Java**

### Option B — Terminal (recommended to see File I/O in action)
```bash
# 1. Go to the src folder
cd NotesApp/src

# 2. Compile
javac NotesApp.java

# 3. Run  (from the src/ folder so notes/ is created here)
java NotesApp
```

### Run the Concepts Reference
```bash
cd NotesApp/src
javac FileIOConcepts.java
java FileIOConcepts
```

---

## 🎮 App Features

| # | Feature | File I/O Used |
|---|---------|---------------|
| 1 | Write a note | `FileWriter` (overwrite) + `BufferedWriter` |
| 2 | List all notes | `BufferedReader` reads `index.txt` |
| 3 | Read a note | `FileReader` + `BufferedReader` line-by-line |
| 4 | Search keyword | `BufferedReader` scans all note files |
| 5 | Delete a note | `File.delete()` + `FileWriter` rewrites index |

---

## 🧠 Key Concepts Learned

### FileWriter
```java
// Overwrite mode (default)
FileWriter fw = new FileWriter("file.txt");

// Append mode
FileWriter fw = new FileWriter("file.txt", true);
```

### BufferedWriter (always wrap FileWriter)
```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter("file.txt"))) {
    bw.write("Hello");
    bw.newLine();   // OS-safe newline
}
```

### FileReader + BufferedReader
```java
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {   // null = EOF
        System.out.println(line);
    }
}
```

### Try-with-resources (auto-close)
```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter("f.txt"))) {
    // bw is automatically closed even if an exception occurs
}
```

### IOException handling
```java
try {
    // file operations
} catch (FileNotFoundException e) {
    System.out.println("File not found: " + e.getMessage());
} catch (IOException e) {
    System.out.println("IO error: " + e.getMessage());
}
```

---

## 📂 How Data Persists

```
notes/
├── index.txt          ← Tracks all notes: "filename|title" per line
├── My_First_Note.txt  ← Individual note files
└── Shopping_List.txt
```

- Each note is saved as its own `.txt` file
- `index.txt` acts as a simple database index
- Data survives between program runs ✅

---

## 💡 Tips
- Type `END` on a new line to finish writing a note
- The `notes/` folder is created automatically next to where you run the app
- Try editing a note file directly in VS Code and re-reading it in the app!
