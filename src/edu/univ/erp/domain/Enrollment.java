package edu.univ.erp.domain;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private String status;
    private String grade;
    private double scoreQuiz;
    private double scoreMidterm;
    private double scoreFinal;

    public Student student;
    public Section section;

    public Enrollment(int enrollmentId, int studentId, int sectionId, String grade) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.grade = grade;
        this.status = "Registered";
    }

    public int getEnrollmentId() { return enrollmentId; }
    public int getStudentId() { return studentId; }
    public int getSectionId() { return sectionId; }
    public String getStatus() { return status; }
    public String getGrade() { return grade; }
    public double getScoreQuiz() { return scoreQuiz; }
    public double getScoreMidterm() { return scoreMidterm; }
    public double getScoreFinal() { return scoreFinal; }

    public void setStatus(String status) { this.status = status; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setScores(double q, double m, double f) { this.scoreQuiz=q; this.scoreMidterm=m; this.scoreFinal=f; }
}
