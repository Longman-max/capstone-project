package smartstudentplatform.util;

import smartstudentplatform.core.StudentManager;
import smartstudentplatform.model.Course;
import smartstudentplatform.model.Student;

import java.io.*;
import java.util.StringTokenizer;

public class FileManager {

    /* -------- Save Students (basic info only) -------- */
    public static void saveStudents(StudentManager manager, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ID,Name,CGPA"); // header
            for (Student s : manager.getAll()) {
                pw.printf("%s,%s,%.2f%n", s.getId(), s.getName(), s.getCgpa());
            }
        }
    }

    /* -------- Load Students (basic info only) -------- */
    public static void loadStudents(StudentManager manager, File file) throws IOException {
        manager.getAll().clear(); // reset existing
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header

                StringTokenizer st = new StringTokenizer(line, ",");
                if (st.countTokens() < 3) continue;

                String id = st.nextToken().trim();
                String name = st.nextToken().trim();
                double cgpa = Double.parseDouble(st.nextToken().trim());

                manager.addStudent(id, name, cgpa);
            }
        }
    }

    /* -------- Save Students with Grades -------- */
    public static void saveStudentsFull(StudentManager manager, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ID,Name,CGPA,Grades"); // header
            for (Student s : manager.getAll()) {
                pw.println(s.toCSV()); // Student#toCSV encodes grades inline
            }
        }
    }

    /* -------- Load Students with Grades -------- */
    public static void loadStudentsFull(StudentManager manager, File file) throws IOException {
        manager.getAll().clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                Student s = Student.fromCSV(line);
                manager.addStudent(s);
            }
        }
    }

    /* -------- Save Results (separate file) -------- */
   public static void saveResults(StudentManager manager, File file) throws IOException {
    try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
        pw.println("ID,CourseCode,Score");
        for (Student s : manager.getAll()) {
            for (var entry : s.getGrades().entrySet()) {
                String courseCode = entry.getKey();   // course code string
                double score = entry.getValue();      // score value
                pw.printf("%s,%s,%.2f%n",
                        s.getId(), courseCode, score);
            }
        }
    }
}

    public static void loadResults(StudentManager manager, File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String id = parts[0].trim();
                String code = parts[1].trim();
                String name = parts[2].trim();
                int credits = Integer.parseInt(parts[3].trim());
                double score = Double.parseDouble(parts[4].trim());

                manager.addResult(id, new Course(code, name, credits), score);
            }
        }
    }
}
