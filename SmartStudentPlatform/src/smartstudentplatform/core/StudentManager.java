package smartstudentplatform.core;

import smartstudentplatform.model.Student;
import smartstudentplatform.model.Course;
import smartstudentplatform.model.Result;
import smartstudentplatform.util.Algorithms;

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
    // class average per courseCode, considering all students that have that course
    public double classAverage(String courseCode) {
        double sum = 0; int n = 0;
        for (Student s : students) {
            Double sc = s.getGrades().get(courseCode);
            if (sc != null) { sum += sc; n++; }
        }
        if (n == 0) throw new IllegalStateException("No scores for course " + courseCode);
        return sum / n;
    }

    // top performer by CGPA
    public Optional<Student> topPerformerByCgpa() {
        return students.stream().max(Comparator.comparingDouble(Student::getCgpa));
    }

    // top performer by average score across all their courses
    public Optional<Student> topPerformerByAvgScore() {
        return students.stream().max(Comparator.comparingDouble(s -> {
            if (s.getGrades().isEmpty()) return -1; // treat as lowest
            double sum = 0;
            for (double v : s.getGrades().values()) sum += v;
            return sum / s.getGrades().size();
        }));
    }
}
