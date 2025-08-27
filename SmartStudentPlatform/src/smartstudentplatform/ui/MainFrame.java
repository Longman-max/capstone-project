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

    // Color scheme
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color SUCCESS = new Color(46, 204, 113);
    private static final Color WARNING = new Color(241, 196, 15);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);

    public MainFrame() {
        super("Smart Student Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) { /* Use default */ }
        
        initializeComponents();
        layoutComponents();
        setJMenuBar(buildMenuBar());
        
        updateStatus("Application started");
    }

    private void initializeComponents() {
        // Configure table with lighter styling
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);
        table.getTableHeader().setBackground(LIGHT_GRAY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        // Configure summary area
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        summaryArea.setBackground(Color.WHITE);
        summaryArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Configure status bar
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statusLabel.setBackground(LIGHT_GRAY);
        statusLabel.setOpaque(true);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Main content with clean spacing
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        // Left side - Student management
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(createStudentFormPanel(), BorderLayout.NORTH);
        leftPanel.add(createTablePanel(), BorderLayout.CENTER);
        leftPanel.add(createSearchSortPanel(), BorderLayout.SOUTH);
        
        // Right side - Compact panels
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(320, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(createResultsPanel(), BorderLayout.NORTH);
        rightPanel.add(createSummaryPanel(), BorderLayout.CENTER);
        rightPanel.add(createAnalyticsPanel(), BorderLayout.SOUTH);
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createStudentFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createLightBorder("Student Management"));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Form fields with cleaner layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(createLabel("Student ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panel.add(styleTextField(idField), gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets.left = 15;
        panel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(styleTextField(nameField), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets.left = 8;
        panel.add(createLabel("CGPA (0-5):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panel.add(styleTextField(cgpaField), gbc);

        // Compact button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setOpaque(false);
        
        buttonPanel.add(createButton("Add Student", SUCCESS, this::onAdd));
        buttonPanel.add(createButton("Update CGPA", PRIMARY, this::onUpdateCgpa));
        buttonPanel.add(createButton("Delete", DANGER, this::onDelete));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.top = 15;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createLightBorder("Student Records"));
        panel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchSortPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createLightBorder("Search & Sort"));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Search section - more compact
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(createLabel("Search ID:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        panel.add(styleTextField(searchIdField), gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets.left = 15;
        JPanel searchBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchBtnPanel.setOpaque(false);
        searchBtnPanel.add(createSmallButton("Linear", WARNING, e -> onSearch(false)));
        searchBtnPanel.add(createSmallButton("Binary", WARNING, e -> onSearch(true)));
        panel.add(searchBtnPanel, gbc);

        // Sort section
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets.left = 8; gbc.insets.top = 5;
        panel.add(createLabel("Sort by:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.insets.left = 15;
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        sortPanel.setOpaque(false);
        
        sortPanel.add(createSmallButton("Name", new Color(155, 89, 182), e -> { 
            manager.sortByNameQuick(); refreshTable(); updateStatus("Sorted by name"); 
        }));
        sortPanel.add(createSmallButton("CGPA", new Color(155, 89, 182), e -> { 
            manager.sortByCgpaBubbleDesc(); refreshTable(); updateStatus("Sorted by CGPA"); 
        }));
        sortPanel.add(createSmallButton("ID", new Color(155, 89, 182), e -> { 
            manager.sortByIdInsertion(); refreshTable(); updateStatus("Sorted by ID"); 
        }));
        
        panel.add(sortPanel, gbc);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(createLightBorder("Course Results"));
        panel.setBackground(Color.WHITE);

        panel.add(createButton("Add Course Result", new Color(52, 152, 219), this::onAddResult), BorderLayout.NORTH);
        
        JPanel filePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        filePanel.setOpaque(false);
        
        filePanel.add(createSmallButton("Save Students", SUCCESS, this::onSaveStudents));
        filePanel.add(createSmallButton("Load Students", SUCCESS, this::onLoadStudents));
        filePanel.add(createSmallButton("Save Results", SUCCESS, this::onSaveResults));
        filePanel.add(createSmallButton("Load Results", SUCCESS, this::onLoadResults));

        panel.add(filePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(createLightBorder("Activity Log"));
        panel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton clearBtn = createSmallButton("Clear Log", new Color(149, 165, 166), 
            e -> { summaryArea.setText(""); updateStatus("Log cleared"); });
        panel.add(clearBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.setBorder(createLightBorder("Analytics"));
        panel.setBackground(Color.WHITE);

        panel.add(createButton("Class Average", new Color(230, 126, 34), this::onClassAverage));
        panel.add(createButton("Top Performer", new Color(230, 126, 34), this::onTopPerformer));

        return panel;
    }

    // Helper methods for cleaner UI components
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JTextField styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(5, 8, 5, 8)
        ));
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return field;
    }

    private JButton createButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        return button;
    }

    private JButton createSmallButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        return button;
    }

    private javax.swing.border.Border createLightBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), 
            title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.PLAIN, 12),
            new Color(52, 73, 94)
        );
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        fileMenu.add(createMenuItem("Save Students...", "ctrl S", this::onSaveStudents));
        fileMenu.add(createMenuItem("Load Students...", "ctrl O", this::onLoadStudents));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Save Results...", null, this::onSaveResults));
        fileMenu.add(createMenuItem("Load Results...", null, this::onLoadResults));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", "ctrl Q", e -> System.exit(0)));

        menuBar.add(fileMenu);
        return menuBar;
    }

    private JMenuItem createMenuItem(String text, String accelerator, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("SansSerif", Font.PLAIN, 12));
        if (accelerator != null) {
            item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        }
        item.addActionListener(action);
        return item;
    }

    /* ---------- Event handlers (unchanged functionality) ---------- */
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
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(createLabel("Course Code:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(styleTextField(codeField), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; panel.add(createLabel("Course Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(styleTextField(nameField), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; panel.add(createLabel("Credits:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(styleTextField(creditsField), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; panel.add(createLabel("Score (0-100):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(styleTextField(scoreField), gbc);

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
