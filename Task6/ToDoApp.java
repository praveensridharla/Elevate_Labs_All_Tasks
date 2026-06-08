import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToDoApp extends JFrame {

    JTextField taskField;
    JButton addButton, deleteButton;
    DefaultListModel<String> taskModel;
    JList<String> taskList;

    public ToDoApp() {

        setTitle("To-Do List App");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Input field
        taskField = new JTextField(20);

        // Buttons
        addButton = new JButton("Add Task");
        deleteButton = new JButton("Delete Task");

        // List
        taskModel = new DefaultListModel<>();
        taskList = new JList<>(taskModel);

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.add(taskField);
        topPanel.add(addButton);

        // Center panel
        JScrollPane scrollPane = new JScrollPane(taskList);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add Task Action
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String task = taskField.getText().trim();

                if (!task.isEmpty()) {
                    taskModel.addElement(task);
                    taskField.setText("");
                } else {
                    JOptionPane.showMessageDialog(
                            ToDoApp.this,
                            "Please enter a task!"
                    );
                }
            }
        });

        // Delete Task Action
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();

                if (selectedIndex != -1) {
                    taskModel.remove(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(
                            ToDoApp.this,
                            "Select a task to delete!"
                    );
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ToDoApp().setVisible(true);
        });
    }
}