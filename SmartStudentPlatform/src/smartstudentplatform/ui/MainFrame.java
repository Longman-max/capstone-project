package smartstudentplatform.ui;

import smartstudentplatform.core.StudentManager;
import smartstudentplatform.model.Course;
import smartstudentplatform.model.Student;
import smartstudentplatform.util.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Optional;

public class MainFrame extends JFrame {
    private final StudentManager manager = new StudentManager();

    // Student form fields
    private final JTextField idField = new JTextField(12);
    private final JTextField nameField = new JTextField(20);
    private final JTextField cgpaField = new JTextField(8);

    // Search field
    private final JTextField searchIdField = new JTextField(12);

    // Table
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Student ID", "Name", "CGPA", "Course Code", "Score"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // Summary area
    private final JTextArea summaryArea = new JTextArea(8, 30);

    // Status bar
    private final JLabel statusLabel = new JLabel("Ready");

    public MainFrame() {
        super("Smart Student Platform - Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        initializeComponents();
        layoutComponents();
        setJMenuBar(buildMenuBar());
        
        updateStatus("Application started");
    }

    private void initializeComponents() {
        // Configure table
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(25);
        
        // Configure summary area
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setBackground(new Color(248, 248, 248));
        
        // Configure status bar
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(240, 240, 240));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Main content area with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Left side - Student management
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(createStudentFormPanel(), BorderLayout.NORTH);
        leftPanel.add(createTablePanel(), BorderLayout.CENTER);
        leftPanel.add(createSearchSortPanel(), BorderLayout.SOUTH);
        
        // Right side - Results and summary
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.add(createResultsPanel(), BorderLayout.NORTH);
        rightPanel.add(createSummaryPanel(), BorderLayout.CENTER);
        rightPanel.add(createAnalyticsPanel(), BorderLayout.SOUTH);
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createStudentFormPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Student Management", 
            0, 0, new Font("SansSerif", Font.BOLD, 12)));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("CGPA (0-5):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(cgpaField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        JButton addBtn = createStyledButton("Add Student", new Color(34, 139, 34));
        addBtn.addActionListener(this::onAdd);
        
        JButton updateBtn = createStyledButton("Update CGPA", new Color(30, 144, 255));
        updateBtn.addActionListener(this::onUpdateCgpa);
        
        JButton deleteBtn = createStyledButton("Delete Student", new Color(220, 20, 60));
        deleteBtn.addActionListener(this::onDelete);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Student Records", 
            0, 0, new Font("SansSerif", Font.BOLD, 12)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchSortPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Search & Sort Options", 
            0, 0, new Font("SansSerif", Font.BOLD, 12)));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Search section
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Search by ID:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(searchIdField, gbc);

        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton linearSearchBtn = createStyledButton("Linear Search", new Color(255, 140, 0));
        JButton binarySearchBtn = createStyledButton("Binary Search", new Color(255, 140, 0));
        linearSearchBtn.addActionListener(e -> onSearch(false));
        binarySearchBtn.addActionListener(e -> onSearch(true));
        searchButtonPanel.add(linearSearchBtn);
        searchButtonPanel.add(binarySearchBtn);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(searchButtonPanel, gbc);

        // Sort section
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        sortPanel.add(new JLabel("Sort by:"));
        
        JButton sortNameBtn = createStyledButton("Name", new Color(138, 43, 226));
        JButton sortCgpaBtn = createStyledButton("CGPA", new Color(138, 43, 226));
        JButton sortIdBtn = createStyledButton("ID", new Color(138, 43, 226));
        
        sortNameBtn.addActionListener(e -> { manager.sortByNameQuick(); refreshTable(); updateStatus("Sorted by name"); });
        sortCgpaBtn.addActionListener(e -> { manager.sortByCgpaBubbleDesc(); refreshTable(); updateStatus("Sorted by CGPA"); });
        sortIdBtn.addActionListener(e -> { manager.sortByIdInsertion(); refreshTable(); updateStatus("Sorted by ID"); });
        
        sortPanel.add(sortNameBtn);
        sortPanel.add(sortCgpaBtn);
        sortPanel.add(sortIdBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sortPanel, gbc);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Course Results", 
            0, 0, new Font("SansSerif", Font.BOLD, 12)));

        JButton addResultBtn = createStyledButton("Add Course Result", new Color(70, 130, 180));
        addResultBtn.addActionListener(this::onAddResult);
        
        JPanel filePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        JButton saveStudentsBtn = createStyledButton("Save Students", new Color(60, 179, 113));
        JButton loadStudentsBtn = createStyledButton("Load Students", new Color(60, 179, 113));
        JButton saveResultsBtn = createStyledButton("Save Results", new Color(60, 179, 113));
        JButton loadResultsBtn = createStyledButton("Load Results", new Color(60, 179, 113));
        
        saveStudentsBtn.addActionListener(this::onSaveStudents);
        loadStudentsBtn.addActionListener(this::onLoadStudents);
        saveResultsBtn.addActionListener(this::onSaveResults);
        loadResultsBtn.addActionListener(this::onLoadResults);
        
        filePanel.add(saveStudentsBtn);
        filePanel.add(loadStudentsBtn);
        filePanel.add(saveResultsBtn);
        filePanel.add(loadResultsBtn);

        panel.add(addResultBtn, BorderLayout.NORTH);
        panel.add(filePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Activity Log", 
            0, 0, new Font("SansSerif", Font.BOLD, 12)));

        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton clearLogBtn = createStyledButton("Clear Log", new Color(192, 192, 192));
        clearLogBtn.addActionListener(e -> { summaryArea.setText(""); updateStatus("Log cleared"); });
        panel.add(clearLogBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Analytics", 
            0, 0, new Font("SansSerif", Font.BOLD, 12)));

        JButton classAvgBtn = createStyledButton("Class Average", new Color(255, 165, 0));
        JButton topPerformerBtn = createStyledButton("Top Performer", new Color(255, 165, 0));
        
        classAvgBtn.addActionListener(this::onClassAverage);
        topPerformerBtn.addActionListener(this::onTopPerformer);

        panel.add(classAvgBtn);
        panel.add(topPerformerBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        return button;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem saveStudentsItem = new JMenuItem("Save Students...");
        saveStudentsItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveStudentsItem.addActionListener(this::onSaveStudents);
        
        JMenuItem loadStudentsItem = new JMenuItem("Load Students...");
        loadStudentsItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        loadStudentsItem.addActionListener(this::onLoadStudents);
        
        JMenuItem saveResultsItem = new JMenuItem("Save Results...");
        saveResultsItem.addActionListener(this::onSaveResults);
        
        JMenuItem loadResultsItem = new JMenuItem("Load Results...");
        loadResultsItem.addActionListener(this::onLoadResults);
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveStudentsItem);
        fileMenu.add(loadStudentsItem);
        fileMenu.addSeparator();
        fileMenu.add(saveResultsItem);
        fileMenu.add(loadResultsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        return menuBar;
    }

    /* ---------- Event handlers ---------- */
    private void onSaveStudents(ActionEvent e) {
        chooseAndDo("Save Students", true, f -> {
            try { 
                FileManager.saveStudents(manager, f); 
                updateStatus("Students saved to " + f.getName());
                updateSummary("✓ Saved students to " + f.getName());
            }
            catch (Exception ex) { 
                error("Save failed: " + ex.getMessage()); 
                updateStatus("Save failed");
            }
        });
    }

    private void onLoadStudents(ActionEvent e) {
        chooseAndDo("Load Students", false, f -> {
            try { 
                FileManager.loadStudents(manager, f); 
                refreshTable(); 
                updateStatus("Students loaded from " + f.getName());
                updateSummary("✓ Loaded students from " + f.getName());
            }
            catch (Exception ex) { 
                error("Load failed: " + ex.getMessage()); 
                updateStatus("Load failed");
            }
        });
    }

    private void onSaveResults(ActionEvent e) {
        chooseAndDo("Save Results", true, f -> {
            try { 
                FileManager.saveResults(manager, f); 
                updateStatus("Results saved to " + f.getName());
                updateSummary("✓ Saved results to " + f.getName());
            }
            catch (Exception ex) { 
                error("Save failed: " + ex.getMessage()); 
                updateStatus("Save failed");
            }
        });
    }

    private void onLoadResults(ActionEvent e) {
        chooseAndDo("Load Results", false, f -> {
            try { 
                FileManager.loadResults(manager, f); 
                refreshTable(); 
                updateStatus("Results loaded from " + f.getName());
                updateSummary("✓ Loaded results from " + f.getName());
            }
            catch (Exception ex) { 
                error("Load failed: " + ex.getMessage()); 
                updateStatus("Load failed");
            }
        });
    }

    private void onAdd(ActionEvent e) {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String cgTxt = cgpaField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || cgTxt.isEmpty()) {
            error("Please fill all required fields (ID, Name, and CGPA).");
            return;
        }
        try {
            double cg = Double.parseDouble(cgTxt);
            if (cg < 0 || cg > 5) { 
                error("CGPA must be between 0 and 5"); 
                return; 
            }
            manager.addStudent(id, name, cg);
            refreshTable();
            clearInputs();
            updateStatus("Student added: " + name);
            updateSummary("✓ Added student: " + name + " (ID: " + id + ", CGPA: " + cg + ")");
        } catch (NumberFormatException ex) {
            error("CGPA must be a valid number.");
        } catch (IllegalArgumentException dup) {
            error(dup.getMessage());
        }
    }

    private void onUpdateCgpa(ActionEvent e) {
        String id = idField.getText().trim();
        String cgTxt = cgpaField.getText().trim();
        if (id.isEmpty() || cgTxt.isEmpty()) { 
            error("Provide Student ID and new CGPA"); 
            return; 
        }
        try {
            double cg = Double.parseDouble(cgTxt);
            if (cg < 0 || cg > 5) { 
                error("CGPA must be between 0 and 5"); 
                return; 
            }
            manager.updateStudentCgpa(id, cg);
            refreshTable();
            updateStatus("CGPA updated for student: " + id);
            updateSummary("✓ Updated CGPA for student " + id + " to " + cg);
        } catch (NumberFormatException ex) {
            error("CGPA must be a valid number.");
        } catch (Exception ex) {
            error(ex.getMessage());
            updateStatus("Update failed");
        }
    }

    private void onDelete(ActionEvent e) {
        String id = idField.getText().trim();
        if (id.isEmpty()) { 
            error("Enter Student ID to delete."); 
            return; 
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete student with ID: " + id + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            manager.removeStudent(id);
            refreshTable();
            clearInputs();
            updateStatus("Student deleted: " + id);
            updateSummary("✗ Deleted student with ID: " + id);
        }
    }

    private void onSearch(boolean binary) {
        String id = searchIdField.getText().trim();
        if (id.isEmpty()) { 
            error("Enter a Student ID to search."); 
            return; 
        }
        Student s = binary ? manager.binarySearch(id) : manager.linearSearch(id);
        String searchType = binary ? "Binary" : "Linear";
        
        if (s == null) {
            info("No student found with ID: " + id);
            updateStatus("Search completed - No results");
            updateSummary("? " + searchType + " search for ID " + id + " - No results");
        } else {
            info("Found: " + s.display());
            updateStatus("Student found: " + s.getName());
            updateSummary("✓ " + searchType + " search found: " + s.getName() + " (ID: " + id + ")");
        }
    }

    private void onAddResult(ActionEvent e) {
        String id = JOptionPane.showInputDialog(this, "Enter Student ID:", "Add Course Result", JOptionPane.QUESTION_MESSAGE);
        if (id == null || id.trim().isEmpty()) return;

        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField creditsField = new JTextField("3");
        JTextField scoreField = new JTextField();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(codeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("Course Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(creditsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("Score (0-100):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(scoreField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Course Result", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int credits = Integer.parseInt(creditsField.getText().trim());
            double score = Double.parseDouble(scoreField.getText().trim());
            
            if (score < 0 || score > 100) {
                error("Score must be between 0 and 100");
                return;
            }
            
            Course course = new Course(codeField.getText().trim(), nameField.getText().trim(), credits);
            manager.addResult(id.trim(), course, score);
            refreshTable();
            updateStatus("Result added for student: " + id);
            updateSummary("✓ Added result for " + id + " - " + codeField.getText().trim() + ": " + score);
        } catch (NumberFormatException ex) {
            error("Credits and Score must be valid numbers.");
        } catch (Exception ex) {
            error(ex.getMessage());
            updateStatus("Failed to add result");
        }
    }

    private void onClassAverage(ActionEvent e) {
        String course = JOptionPane.showInputDialog(this, "Enter Course Code (e.g., COS201):", "Class Average", JOptionPane.QUESTION_MESSAGE);
        if (course == null || course.trim().isEmpty()) return;
        
        try {
            double avg = manager.classAverage(course.trim());
            String message = String.format("Class average for %s: %.2f", course.trim(), avg);
            info(message);
            updateSummary("📊 " + message);
            updateStatus("Class average calculated");
        } catch (Exception ex) {
            error(ex.getMessage());
            updateStatus("Failed to calculate average");
        }
    }

    private void onTopPerformer(ActionEvent e) {
        Object[] options = {"By CGPA", "By Average Score", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Select metric for top performer:", 
            "Top Performer Analysis",
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);
            
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) return;

        Optional<Student> student = (choice == 0) ? 
            manager.topPerformerByCgpa() : 
            manager.topPerformerByAvgScore();
            
        if (student.isPresent()) {
            String metric = (choice == 0) ? "CGPA" : "Average Score";
            String message = "Top performer by " + metric + ": " + student.get().display();
            info(message);
            updateSummary("🏆 " + message);
            updateStatus("Top performer identified");
        } else {
            info("No students found in the system.");
            updateStatus("No students available");
        }
    }

    /* ---------- Helper methods ---------- */
    private void refreshTable() {
        tableModel.setRowCount(0);
        int studentCount = 0;
        
        for (Student s : manager.getAll()) {
            studentCount++;
            if (s.getGrades().isEmpty()) {
                tableModel.addRow(new Object[]{
                    s.getId(), 
                    s.getName(), 
                    String.format("%.2f", s.getCgpa()), 
                    "-", 
                    "-"
                });
            } else {
                s.getGrades().forEach((courseCode, score) -> {
                    tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        String.format("%.2f", s.getCgpa()),
                        courseCode,
                        String.format("%.1f", score)
                    });
                });
            }
        }
        
        updateStatus("Displaying " + studentCount + " students with " + tableModel.getRowCount() + " total records");
    }

    private void clearInputs() {
        idField.setText("");
        nameField.setText("");
        cgpaField.setText("");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void updateSummary(String message) {
        summaryArea.append(java.time.LocalTime.now().toString().substring(0, 8) + " - " + message + "\n");
        summaryArea.setCaretPosition(summaryArea.getDocument().getLength());
    }

    private interface FileAction {
        void run(File f);
    }

    private void chooseAndDo(String title, boolean save, FileAction action) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        
        if (title.toLowerCase().contains("csv")) {
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        }
        
        int result = save ? chooser.showSaveDialog(this) : chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            action.run(chooser.getSelectedFile());
        }
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
