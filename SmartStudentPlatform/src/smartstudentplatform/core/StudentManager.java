package smartstudentplatform.core;

import smartstudentplatform.model.Student;
import smartstudentplatform.model.Course;
import smartstudentplatform.util.Algorithms;

import java.io.*;
import java.util.*;

public class StudentManager {
    private final List<Student> students = new ArrayList<>();             // ordered list
    private final Map<String, Student> indexById = new HashMap<>();       // fast lookup

    public List<Student> getAll() { return students; }

    /* -------- Add / Update -------- */
    public void addStudent(Student s) {
        if (indexById.containsKey(s.getId()))
            throw new IllegalArgumentException("Student with ID " + s.getId() + " already exists");
        students.add(s);
        indexById.put(s.getId(), s);
    }

    public void addStudent(String id, String name, double cgpa) {
        addStudent(new Student(id, name, cgpa)); // overloaded form
    }

    public void updateStudentCgpa(String id, double newCgpa) {
        Student s = indexById.get(id);
        if (s == null) throw new NoSuchElementException("No student with ID " + id);
        s.setCgpa(newCgpa);
    }

    public void removeStudent(String id) {
        Student s = indexById.remove(id);
        if (s != null) students.remove(s);
    }

    /* -------- Searching -------- */
    public Student linearSearch(String id) {
        return Algorithms.linearSearchById(students, id);
    }

    public Student binarySearch(String id) {
        Algorithms.insertionSortById(students); // ensure sorted by ID
        return Algorithms.binarySearchById(students, id);
    }

    /* -------- Sorting -------- */
    public void sortByNameQuick() { Algorithms.quickSortByName(students); }
    public void sortByCgpaBubbleDesc() { Algorithms.bubbleSortByCgpa(students); }
    public void sortByIdInsertion() { Algorithms.insertionSortById(students); }

    /* -------- Results (grades) -------- */
    public void addResult(String studentId, Course course, double score) {
        Student s = indexById.get(studentId);
        if (s == null) throw new NoSuchElementException("No student with ID " + studentId);
        if (score < 0 || score > 100) throw new IllegalArgumentException("Score must be 0..100");
        s.addGrade(course.getCode(), course.getName(), score);
    }

    /* -------- Summaries -------- */
    public double classAverage(String courseCode) {
        double sum = 0; int n = 0;
        for (Student s : students) {
            Double sc = s.getGrades().get(courseCode);
            if (sc != null) { sum += sc; n++; }
        }
        if (n == 0) throw new IllegalStateException("No scores for course " + courseCode);
        return sum / n;
    }

    public Optional<Student> topPerformerByCgpa() {
        return students.stream().max(Comparator.comparingDouble(Student::getCgpa));
    }

    public Optional<Student> topPerformerByAvgScore() {
        return students.stream().max(Comparator.comparingDouble(s -> {
            if (s.getGrades().isEmpty()) return -1; // treat as lowest
            double sum = 0;
            for (double v : s.getGrades().values()) sum += v;
            return sum / s.getGrades().size();
        }));
    }

    /* -------- CSV File Handling -------- */
    public void saveToCSV(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("ID,Name,CGPA\n"); // header
            for (Student s : students) {
                writer.append(s.getId()).append(",");
                writer.append(s.getName()).append(",");
                writer.append(String.valueOf(s.getCgpa())).append("\n");
            }
        }
    }

    public void loadFromCSV(File file) throws IOException {
        students.clear();
        indexById.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String id = values[0].trim();
                    String name = values[1].trim();
                    double cgpa = Double.parseDouble(values[2].trim());
                    addStudent(new Student(id, name, cgpa));
                }
            }
        }
    }
}
