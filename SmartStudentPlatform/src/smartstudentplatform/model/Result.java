package smartstudentplatform.model;

public class Result {
    private final Student student;
    private final Course course;
    private final double score; // 0-100

    public Result(Student student, Course course, double score) {
        this.student = student;
        this.course = course;
        this.score = score;
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public double getScore() { return score; }

    @Override
    public String toString() {
        return student.getName() + " - " + course.getCode() + ": " + score;
    }
}
