package smartstudentplatform.util;

import smartstudentplatform.core.StudentManager;
import smartstudentplatform.model.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    // Define a header for the CSV file. This should match the format from Student.toCSV()
    private static final String CSV_HEADER = "ID,Name,CGPA,Grades";

    /**
     * Saves all student data to a CSV file using the Student.toCSV() method.
     * Each student is saved on a single line.
     */
    public static void saveAllData(StudentManager manager, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(CSV_HEADER); // Write the header
            for (Student student : manager.getAll()) {
                writer.println(student.toCSV()); // Use the toCSV method from the Student class
            }
        }
    }

    /**
     * Loads student data from a CSV file using the Student.fromCSV() method.
     */
    public static void loadAllData(StudentManager manager, File file) throws IOException, ClassNotFoundException {
        List<Student> loadedStudents = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Skip the header line

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip any blank lines
                }
                try {
                    // Use the fromCSV method from the Student class to create a student object
                    Student student = Student.fromCSV(line);
                    loadedStudents.add(student);
                } catch (Exception e) {
                    // If a line in the CSV is badly formatted, we print an error and continue
                    System.err.println("Skipping malformed CSV line: " + line + " | Error: " + e.getMessage());
                }
            }
        }

        // Clear the manager's current data and add all the newly loaded students
        manager.getAll().clear();
        manager.getAll().addAll(loadedStudents);
    }
}