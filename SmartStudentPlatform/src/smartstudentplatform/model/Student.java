package smartstudentplatform.model;

import java.util.HashMap;
import java.util.Map;

public class Student extends Person {
    private double cgpa;                       // encapsulated field
    private final Map<String, Double> grades;  // courseCode -> score (0-100)

    public Student(String id, String name, double cgpa) {
        super(id, name);
        this.cgpa = cgpa;
        this.grades = new HashMap<>();
    }

    // Overloading: addGrade with/without course name (demonstrate method overloading)
    public void addGrade(String courseCode, double score) { grades.put(courseCode, score); }
    public void addGrade(String courseCode, String _courseNameIgnored, double score) { grades.put(courseCode, score); }

    public Map<String, Double> getGrades() { return grades; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    // Overriding the base display formatting
    @Override
    public String display() {
        return super.display() + " | CGPA: " + String.format("%.2f", cgpa);
    }
}
