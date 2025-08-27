package smartstudentplatform.ui;

import smartstudentplatform.core.StudentManager;
import smartstudentplatform.model.Course;
import smartstudentplatform.model.Student;
import smartstudentplatform.util.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Optional;

public class MainFrame extends JFrame {
  private final StudentManager manager = new StudentManager();

  // Modern color scheme
  private static final Color PRIMARY_COLOR = new Color(47, 54, 64);
  private static final Color SECONDARY_COLOR = new Color(75, 123, 236);
  private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
  private static final Color WARNING_COLOR = new Color(241, 196, 15);
  private static final Color DANGER_COLOR = new Color(231, 76, 60);
  private static final Color LIGHT_GRAY = new Color(248, 249, 250);
  private static final Color BORDER_COLOR = new Color(220, 221, 225);
  private static final Color TEXT_COLOR = new Color(52, 58, 64);

  // Student form fields
  private final JTextField idField = createModernTextField();
  private final JTextField nameField = createModernTextField();
  private final JTextField cgpaField = createModernTextField();

  // Search field
  private final JTextField searchIdField = createModernTextField();

  // Table
  private final DefaultTableModel tableModel=new DefaultTableModel(new String[]{"ID","Student Name","CGPA","Course","Score"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
  private final JTable table = new JTable(tableModel);

  // Summary area
  private final JTextArea summaryArea = new JTextArea(8, 30);

  // Status bar
  private final JLabel statusLabel = new JLabel("Ready");

  public MainFrame() {
    super("Smart Student Platform");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1300, 800);
    setLocationRelativeTo(null);

    // Set modern look and feel
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
    } catch (Exception e) {
      e.printStackTrace();
    }

    initializeComponents();
    layoutComponents();
    setJMenuBar(buildModernMenuBar());

    updateStatus("Application started successfully");
  }

  private JTextField createModernTextField() {
    JTextField field = new JTextField();
    field.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
    field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    field.setBackground(Color.WHITE);
    return field;
  }

  private void initializeComponents() {
    // Configure table with modern styling
    table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    table.setRowHeight(35);
    table.setGridColor(BORDER_COLOR);
    table.setSelectionBackground(
        new Color(SECONDARY_COLOR.getRed(), SECONDARY_COLOR.getGreen(), SECONDARY_COLOR.getBlue(), 30));
    table.setSelectionForeground(TEXT_COLOR);
    table.setShowVerticalLines(true);
    table.setShowHorizontalLines(true);

    // Modern table header
    table.getTableHeader().setBackground(PRIMARY_COLOR);
    table.getTableHeader().setForeground(Color.WHITE);
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    table.getTableHeader().setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    table.getTableHeader().setReorderingAllowed(false);

    // Alternating row colors
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
          c.setBackground(row % 2 == 0 ? Color.WHITE : LIGHT_GRAY);
        }
        return c;
      }
    });

    // Configure summary area with modern styling
    summaryArea.setEditable(false);
    summaryArea.setFont(new Font("Consolas", Font.PLAIN, 12));
    summaryArea.setBackground(new Color(45, 52, 54));
    summaryArea.setForeground(new Color(220, 221, 225));
    summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Configure status bar
    statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
    statusLabel.setOpaque(true);
    statusLabel.setBackground(PRIMARY_COLOR);
    statusLabel.setForeground(Color.WHITE);
    statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
  }

  private void layoutComponents() {
    setLayout(new BorderLayout());
    getContentPane().setBackground(LIGHT_GRAY);

    // Create main container with padding
    JPanel mainContainer = new JPanel(new BorderLayout(15, 15));
    mainContainer.setBackground(LIGHT_GRAY);
    mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Top section - Header
    JPanel headerPanel = createHeaderPanel();

    // Center section - Main content in cards
    JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
    centerPanel.setBackground(LIGHT_GRAY);

    // Left card - Student Management
    JPanel leftCard = createCard("Student Management");
    leftCard.setLayout(new BorderLayout(10, 10));
    leftCard.add(createStudentFormPanel(), BorderLayout.NORTH);
    leftCard.add(createTablePanel(), BorderLayout.CENTER);
    leftCard.add(createSearchPanel(), BorderLayout.SOUTH);

    // Right card - Operations
    JPanel rightCard = createCard("Operations & Analytics");
    rightCard.setLayout(new BorderLayout(10, 10));
    rightCard.add(createQuickActionsPanel(), BorderLayout.NORTH);
    rightCard.add(createLogPanel(), BorderLayout.CENTER);
    rightCard.add(createAnalyticsPanel(), BorderLayout.SOUTH);

    centerPanel.add(leftCard);
    centerPanel.add(rightCard);

    mainContainer.add(headerPanel, BorderLayout.NORTH);
    mainContainer.add(centerPanel, BorderLayout.CENTER);

    add(mainContainer, BorderLayout.CENTER);
    add(statusLabel, BorderLayout.SOUTH);
  }

  private JPanel createHeaderPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(LIGHT_GRAY);
    panel.setBorder(new EmptyBorder(0, 0, 15, 0));

    JLabel titleLabel = new JLabel("Student Management System");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(PRIMARY_COLOR);

    JLabel subtitleLabel = new JLabel("Manage students, grades, and analytics efficiently");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitleLabel.setForeground(new Color(108, 117, 125));

    JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.setBackground(LIGHT_GRAY);
    titlePanel.add(titleLabel, BorderLayout.CENTER);
    titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

    panel.add(titlePanel, BorderLayout.WEST);
    return panel;
  }

  private JPanel createCard(String title) {
    JPanel card = new JPanel();
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(BORDER_COLOR, 1),
        new EmptyBorder(15, 15, 15, 15)));

    // Add subtle shadow effect
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 10)),
        BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15))));

    return card;
  }

  private JPanel createStudentFormPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 0, 8, 15);

    // Section header
    JLabel headerLabel = new JLabel("Add New Student");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    headerLabel.setForeground(PRIMARY_COLOR);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    panel.add(headerLabel, gbc);

    // Form fields with labels
    gbc.gridwidth = 1;
    gbc.gridy++;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    panel.add(createFieldLabel("Student ID"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    panel.add(idField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    panel.add(createFieldLabel("Full Name"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    panel.add(nameField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    panel.add(createFieldLabel("CGPA (0.0 - 5.0)"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    panel.add(cgpaField, gbc);

    // Action buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    buttonPanel.setBackground(Color.WHITE);

    buttonPanel.add(createModernButton("Add Student", SUCCESS_COLOR, this::onAdd));
    buttonPanel.add(createModernButton("Update CGPA", SECONDARY_COLOR, this::onUpdateCgpa));
    buttonPanel.add(createModernButton("Delete", DANGER_COLOR, this::onDelete));

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(15, 0, 0, 0);
    panel.add(buttonPanel, gbc);

    return panel;
  }

  private JLabel createFieldLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Segoe UI", Font.MEDIUM, 13));
    label.setForeground(TEXT_COLOR);
    return label;
  }

  private JPanel createTablePanel() {
    JPanel panel = new JPanel(new BorderLayout(0, 10));
    panel.setBackground(Color.WHITE);

    JLabel headerLabel = new JLabel("Student Records");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    headerLabel.setForeground(PRIMARY_COLOR);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
    scrollPane.setPreferredSize(new Dimension(0, 250));

    panel.add(headerLabel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JPanel createSearchPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(Color.WHITE);

    JLabel headerLabel = new JLabel("Search & Sort");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    headerLabel.setForeground(PRIMARY_COLOR);

    // Search section
    JPanel searchSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    searchSection.setBackground(Color.WHITE);
    searchSection.add(createFieldLabel("Search ID:"));
    searchIdField.setPreferredSize(new Dimension(150, 35));
    searchSection.add(searchIdField);
    searchSection.add(createModernButton("Linear", WARNING_COLOR, e -> onSearch(false)));
    searchSection.add(createModernButton("Binary", WARNING_COLOR, e -> onSearch(true)));

    // Sort section
    JPanel sortSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    sortSection.setBackground(Color.WHITE);
    sortSection.add(createFieldLabel("Sort by:"));
    sortSection.add(createModernButton("Name", new Color(155, 89, 182), e -> {
      manager.sortByNameQuick();
      refreshTable();
      updateStatus("Sorted by name");
    }));
    sortSection.add(createModernButton("CGPA", new Color(155, 89, 182), e -> {
      manager.sortByCgpaBubbleDesc();
      refreshTable();
      updateStatus("Sorted by CGPA");
    }));
    sortSection.add(createModernButton("ID", new Color(155, 89, 182), e -> {
      manager.sortByIdInsertion();
      refreshTable();
      updateStatus("Sorted by ID");
    }));

    JPanel controlsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
    controlsPanel.setBackground(Color.WHITE);
    controlsPanel.add(searchSection);
    controlsPanel.add(sortSection);

    panel.add(headerLabel, BorderLayout.NORTH);
    panel.add(controlsPanel, BorderLayout.CENTER);

    return panel;
  }

  private JPanel createQuickActionsPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel headerLabel = new JLabel("Quick Actions");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    headerLabel.setForeground(PRIMARY_COLOR);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    panel.add(headerLabel, gbc);

    gbc.gridwidth = 1;
    gbc.weightx = 1.0;

    // Course results
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(createModernButton("Add Course Result", new Color(52, 152, 219), this::onAddResult), gbc);
    gbc.gridx = 1;
    panel.add(createModernButton("Class Average", WARNING_COLOR, this::onClassAverage), gbc);

    // File operations
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(createModernButton("Save Students", SUCCESS_COLOR, this::onSaveStudents), gbc);
    gbc.gridx = 1;
    panel.add(createModernButton("Load Students", SUCCESS_COLOR, this::onLoadStudents), gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(createModernButton("Save Results", new Color(26, 188, 156), this::onSaveResults), gbc);
    gbc.gridx = 1;
    panel.add(createModernButton("Load Results", new Color(26, 188, 156), this::onLoadResults), gbc);

    return panel;
  }

  private JPanel createLogPanel() {
    JPanel panel = new JPanel(new BorderLayout(0, 10));
    panel.setBackground(Color.WHITE);

    JLabel headerLabel = new JLabel("Activity Log");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    headerLabel.setForeground(PRIMARY_COLOR);

    JScrollPane scrollPane = new JScrollPane(summaryArea);
    scrollPane.setBorder(new LineBorder(new Color(52, 58, 64), 1));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    JButton clearBtn = createModernButton("Clear Log", new Color(108, 117, 125),
        e -> {
          summaryArea.setText("");
          updateStatus("Log cleared");
        });

    panel.add(headerLabel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(clearBtn, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createAnalyticsPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    panel.setBackground(Color.WHITE);

    JButton topPerformerBtn = createModernButton("Top Performer", new Color(230, 126, 34), this::onTopPerformer);
    panel.add(topPerformerBtn);

    return panel;
  }

  private JButton createModernButton(String text, Color color, java.awt.event.ActionListener listener) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Segoe UI", Font.MEDIUM, 12));
    button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.addActionListener(listener);

    // Hover effect
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        button.setBackground(color.brighter());
      }

      @Override
      public void mouseExited(java.awt.event.MouseEvent evt) {
        button.setBackground(color);
      }
    });

    return button;
  }

  private JMenuBar buildModernMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    menuBar.setBackground(Color.WHITE);
    menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

    JMenu fileMenu = new JMenu("File");
    fileMenu.setFont(new Font("Segoe UI", Font.MEDIUM, 12));
    fileMenu.setForeground(TEXT_COLOR);

    JMenuItem saveStudentsItem = createMenuItem("Save Students...", "ctrl S", this::onSaveStudents);
    JMenuItem loadStudentsItem = createMenuItem("Load Students...", "ctrl O", this::onLoadStudents);
    JMenuItem saveResultsItem = createMenuItem("Save Results...", null, this::onSaveResults);
    JMenuItem loadResultsItem = createMenuItem("Load Results...", null, this::onLoadResults);
    JMenuItem exitItem = createMenuItem("Exit", "ctrl Q", e -> System.exit(0));

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

  private JMenuItem createMenuItem(String text, String accelerator, java.awt.event.ActionListener listener) {
    JMenuItem item = new JMenuItem(text);
    item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    if (accelerator != null) {
      item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
    }
    item.addActionListener(listener);
    return item;
  }

  /* ---------- Event handlers (unchanged functionality) ---------- */
  private void onSaveStudents(ActionEvent e) {
    chooseAndDo("Save Students", true, f -> {
      try {
        FileManager.saveStudents(manager, f);
        updateStatus("Students saved to " + f.getName());
        updateSummary("✓ Saved students to " + f.getName());
      } catch (Exception ex) {
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
      } catch (Exception ex) {
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
      } catch (Exception ex) {
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
      } catch (Exception ex) {
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
      updateStatus("Failed to calculate average");
    }
  }

  private void onTopPerformer(ActionEvent e) {
    Object[] options = { "By CGPA", "By Average Score", "Cancel" };
    int choice = JOptionPane.showOptionDialog(this,
        "Select metric for top performer:",
        "Top Performer Analysis",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null, options, options[0]);

    if (choice == 2 || choice == JOptionPane.CLOSED_OPTION)
      return;

    Optional<Student> student = (choice == 0) ? manager.topPerformerByCgpa() : manager.topPerformerByAvgScore();

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
        tableModel.addRow(new Object[] {
            s.getId(),
            s.getName(),
            String.format("%.2f", s.getCgpa()),
            "-",
            "-"
        });
      } else {
        s.getGrades().forEach((courseCode, score) -> {
          tableModel.addRow(new Object[] {
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

}getMessage());updateStatus("Update failed");}}

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
    String id = JOptionPane.showInputDialog(this, "Enter Student ID:", "Add Course Result",
        JOptionPane.QUESTION_MESSAGE);
    if (id == null || id.trim().isEmpty())
      return;

    JTextField codeField = createModernTextField();
    JTextField nameField = createModernTextField();
    JTextField creditsField = createModernTextField();
    creditsField.setText("3");
    JTextField scoreField = createModernTextField();

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(createFieldLabel("Course Code:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(codeField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    panel.add(createFieldLabel("Course Name:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(nameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.NONE;
    panel.add(createFieldLabel("Credits:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(creditsField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.NONE;
    panel.add(createFieldLabel("Score (0-100):"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(scoreField, gbc);

    int result = JOptionPane.showConfirmDialog(this, panel, "Add Course Result", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION)
      return;

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
            error(ex.
