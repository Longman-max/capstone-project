package smartstudentplatform.util;

import smartstudentplatform.core.StudentManager;
import smartstudentplatform.model.Student;
import smartstudentplatform.model.Course;

import java.io.*;
import java.util.List;

public class FileManager {

    /* --- Save Students to CSV --- */
    public static void saveStudents(StudentManager manager, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Student s : manager.getAll()) {
                writer.write(s.getId() + "," + s.getName() + "," + s.getCgpa());
                writer.newLine();
            }
        }
    }

    /* --- Load Students from CSV --- */
    public static void loadStudents(StudentManager manager, File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String id = parts[0];
                    String name = parts[1];
                    double cgpa = Double.parseDouble(parts[2]);
                    try {
                        manager.addStudent(id, name, cgpa);
                    } catch (IllegalArgumentException ex) {
                        // skip duplicates
                    }
                }
            }
        }
    }

    /* --- Save Results (grades) to CSV --- */
    public static void saveResults(StudentManager manager, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Student s : manager.getAll()) {
                for (var entry : s.getGrades().entrySet()) {
                    String courseCode = entry.getKey();
                    double score = entry.getValue();
                    // Store format: studentId, courseCode, score
                    writer.write(s.getId() + "," + courseCode + "," + score);
                    writer.newLine();
                }
            }
        }
    }

    /* --- Load Results (grades) from CSV --- */
    public static void loadResults(StudentManager manager, File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String studentId = parts[0];
                    String courseCode = parts[1];
                    double score = Double.parseDouble(parts[2]);

                    // Create dummy course since we only stored courseCode
                    Course course = new Course(courseCode, "Unknown", 3);

                    try {
                        manager.addResult(studentId, course, score);
                    } catch (Exception ex) {
                        // ignore errors for missing students or invalid scores
                    }
                }
            }
        }
    }
}
