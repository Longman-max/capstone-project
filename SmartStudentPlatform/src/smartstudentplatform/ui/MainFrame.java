package smartstudentplatform.ui;

import smartstudentplatform.core.StudentManager;
import smartstudentplatform.model.Course;
import smartstudentplatform.model.Student;
import smartstudentplatform.util.FileManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Optional;

public class MainFrame extends JFrame {
    private final StudentManager manager = new StudentManager();

    private final JTextField idField = new JTextField(8);
    private final JTextField nameField = new JTextField(16);
    private final JTextField cgpaField = new JTextField(6);

    private final JTextField searchIdField = new JTextField(8);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "CGPA"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public MainFrame() {
        super("Smart Student Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
        setJMenuBar(buildMenu());

        // demo data (optional)
        // manager.addStudent("COS001", "Ada Lovelace", 4.77); refreshTable();
    }

    /* ---------- UI Builders ---------- */
    private JPanel buildTopPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createTitledBorder("Add / Update Student"));

        p.add(new JLabel("ID:"));    p.add(idField);
        p.add(new JLabel("Name:"));  p.add(nameField);
        p.add(new JLabel("CGPA:"));  p.add(cgpaField);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> onAdd());
        JButton updateBtn = new JButton("Update CGPA");
        updateBtn.addActionListener(e -> onUpdateCgpa());
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> onDelete());

        p.add(addBtn); p.add(updateBtn); p.add(deleteBtn);
        return p;
    }

    private JPanel buildBottomPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createTitledBorder("Tools"));

        // search
        p.add(new JLabel("Search ID:"));
        p.add(searchIdField);
        JButton linSearch = new JButton("Linear Search");
        linSearch.addActionListener(e -> onSearch(false));
        JButton binSearch = new JButton("Binary Search");
        binSearch.addActionListener(e -> onSearch(true));
        p.add(linSearch); p.add(binSearch);

        // sort choices
        JButton sortName = new JButton("Sort by Name (QuickSort)");
        sortName.addActionListener(e -> { manager.sortByNameQuick(); refreshTable(); });

        JButton sortCgpa = new JButton("Sort by CGPA (Bubble)");
        sortCgpa.addActionListener(e -> { manager.sortByCgpaBubbleDesc(); refreshTable(); });

        JButton sortId = new JButton("Sort by ID (Insertion)");
        sortId.addActionListener(e -> { manager.sortByIdInsertion(); refreshTable(); });

        p.add(sortName); p.add(sortCgpa); p.add(sortId);

        // results & summary
        JButton addResult = new JButton("Add Result");
        addResult.addActionListener(e -> onAddResult());
        JButton avgBtn = new JButton("Class Average");
        avgBtn.addActionListener(e -> onClassAverage());
        JButton topBtn = new JButton("Top Performer");
        topBtn.addActionListener(e -> onTopPerformer());

        p.add(addResult); p.add(avgBtn); p.add(topBtn);

        return p;
    }

    private JMenuBar buildMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");

        JMenuItem saveStudents = new JMenuItem("Save Students...");
        saveStudents.addActionListener(e -> chooseAndDo("Save Students", true, f -> {
            try { FileManager.saveStudents(manager, f); info("Saved students to " + f.getName()); }
            catch (Exception ex) { error("Save failed: " + ex.getMessage()); }
        }));

        JMenuItem loadStudents = new JMenuItem("Load Students...");
        loadStudents.addActionListener(e -> chooseAndDo("Load Students", false, f -> {
            try { FileManager.loadStudents(manager, f); refreshTable(); info("Loaded " + f.getName()); }
            catch (Exception ex) { error("Load failed: " + ex.getMessage()); }
        }));

        JMenuItem saveResults = new JMenuItem("Save Results...");
        saveResults.addActionListener(e -> chooseAndDo("Save Results", true, f -> {
            try { FileManager.saveResults(manager, f); info("Saved results to " + f.getName()); }
            catch (Exception ex) { error("Save failed: " + ex.getMessage()); }
        }));

        JMenuItem loadResults = new JMenuItem("Load Results...");
        loadResults.addActionListener(e -> chooseAndDo("Load Results", false, f -> {
            try { FileManager.loadResults(manager, f); info("Loaded " + f.getName()); }
            catch (Exception ex) { error("Load failed: " + ex.getMessage()); }
        }));

        file.add(saveStudents); file.add(loadStudents);
        file.addSeparator();
        file.add(saveResults); file.add(loadResults);

        mb.add(file);
        return mb;
    }

    /* ---------- Event handlers (ActionListener / ItemListener used) ---------- */
    private void onAdd() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String cgTxt = cgpaField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || cgTxt.isEmpty()) {
            error("Please fill ID, Name and CGPA.");
            return;
        }
        try {
            double cg = Double.parseDouble(cgTxt);
            if (cg < 0 || cg > 5) { error("CGPA must be between 0 and 5"); return; }
            manager.addStudent(id, name, cg);
            refreshTable();
            clearInputs();
        } catch (NumberFormatException ex) {
            error("CGPA must be numeric.");
        } catch (IllegalArgumentException dup) {
            error(dup.getMessage());
        }
    }

    private void onUpdateCgpa() {
        String id = idField.getText().trim();
        String cgTxt = cgpaField.getText().trim();
        if (id.isEmpty() || cgTxt.isEmpty()) { error("Provide ID and new CGPA"); return; }
        try {
            double cg = Double.parseDouble(cgTxt);
            manager.updateStudentCgpa(id, cg);
            refreshTable();
        } catch (NumberFormatException ex) {
            error("CGPA must be numeric.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void onDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) { error("Enter ID to delete."); return; }
        manager.removeStudent(id);
        refreshTable();
    }

    private void onSearch(boolean binary) {
        String id = searchIdField.getText().trim();
        if (id.isEmpty()) { error("Enter an ID to search."); return; }
        Student s = binary ? manager.binarySearch(id) : manager.linearSearch(id);
        if (s == null) info("No student found with ID " + id);
        else info("Found: " + s.display());
    }

    private void onAddResult() {
        String id = JOptionPane.showInputDialog(this, "Student ID for result:");
        if (id == null || id.isEmpty()) return;

        JTextField code = new JTextField();
        JTextField name = new JTextField();
        JTextField credits = new JTextField("3");
        JTextField score = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Course Code:")); panel.add(code);
        panel.add(new JLabel("Course Name:")); panel.add(name);
        panel.add(new JLabel("Credits:"));     panel.add(credits);
        panel.add(new JLabel("Score (0-100):")); panel.add(score);

        int ok = JOptionPane.showConfirmDialog(this, panel, "Add Result", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int cr = Integer.parseInt(credits.getText().trim());
            double sc = Double.parseDouble(score.getText().trim());
            manager.addResult(id, new Course(code.getText().trim(), name.getText().trim(), cr), sc);
            info("Result added.");
        } catch (NumberFormatException ex) {
            error("Credits and Score must be numeric.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void onClassAverage() {
        String course = JOptionPane.showInputDialog(this, "Course code (e.g., COS201):");
        if (course == null || course.isEmpty()) return;
        try {
            double avg = manager.classAverage(course.trim());
            info("Class average for " + course + " = " + String.format("%.2f", avg));
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void onTopPerformer() {
        Object[] options = { "By CGPA", "By Average Score", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this, "Select metric:", "Top Performer",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) return;

        Optional<Student> s = (choice == 0) ? manager.topPerformerByCgpa() : manager.topPerformerByAvgScore();
        if (s.isPresent()) info("Top performer: " + s.get().display());
        else info("No students yet.");
    }

    /* ---------- helpers ---------- */
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student s : manager.getAll())
            tableModel.addRow(new Object[]{ s.getId(), s.getName(), String.format("%.2f", s.getCgpa()) });
    }

    private void clearInputs() { idField.setText(""); nameField.setText(""); cgpaField.setText(""); }

    private interface FileAction { void run(File f); }
    private void chooseAndDo(String title, boolean save, FileAction action) {
        JFileChooser ch = new JFileChooser();
        ch.setDialogTitle(title);
        int res = save ? ch.showSaveDialog(this) : ch.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) action.run(ch.getSelectedFile());
    }

    private void info(String msg)  { JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}
