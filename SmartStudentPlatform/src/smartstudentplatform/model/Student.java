package smartstudentplatform.model;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Student extends Person {
    private double cgpa;                           // encapsulated field
    private final Map<String, Double> grades;      // courseCode -> score (0-100)
    private final Map<String, String> courseNames; // courseCode -> courseName

    public Student(String id, String name, double cgpa) {
        super(id, name);
        this.cgpa = cgpa;
        this.grades = new HashMap<>();
        this.courseNames = new HashMap<>();
    }

    // Add grade by courseCode only
    public void addGrade(String courseCode, double score) {
        grades.put(courseCode, score);
    }

    // Add grade with both code and name
    public void addGrade(String courseCode, String courseName, double score) {
        grades.put(courseCode, score);
        courseNames.put(courseCode, courseName);
    }

    // Getters
    public Map<String, Double> getGrades() { return grades; }
    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public String getCourseName(String courseCode) {
        return courseNames.getOrDefault(courseCode, "Unknown Course");
    }

    @Override
    public String display() {
        return super.display() + " | CGPA: " + String.format("%.2f", cgpa);
    }

    /* =====================================================
       CSV HANDLING
       ===================================================== */

    /** Convert Student object to CSV row */
    public String toCSV() {
        StringJoiner gradeJoiner = new StringJoiner(";");
        for (Map.Entry<String, Double> entry : grades.entrySet()) {
            String code = entry.getKey();
            double score = entry.getValue();
            String name = getCourseName(code);
            gradeJoiner.add(code + ":" + score + ":" + name);
        }

        return String.join(",",
                getId(),
                getName(),
                String.valueOf(cgpa),
                gradeJoiner.toString()
        );
    }

    /** Create a Student object from a CSV row */
    public static Student fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid CSV line: " + csvLine);
        }

        String id = parts[0];
        String name = parts[1];
        double cgpa = Double.parseDouble(parts[2]);

        Student student = new Student(id, name, cgpa);

        if (parts.length > 3 && !parts[3].isEmpty()) {
            String[] gradeTokens = parts[3].split(";");
            for (String token : gradeTokens) {
                String[] kv = token.split(":");
                if (kv.length >= 2) {
                    String courseCode = kv[0];
                    double score = Double.parseDouble(kv[1]);
                    String courseName = kv.length == 3 ? kv[2] : "Unknown Course";
                    student.addGrade(courseCode, courseName, score);
                }
            }
        }
        return student;
    }
}
