// File: edu.univ.erp.domain.Enrollment.java

package edu.univ.erp.domain;

/**
 * Represents a student enrollment in a section (university_erp_db.enrollments table).
 */
public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private String status; // e.g., "Registered"
    private String grade;  // e.g., "A", "B+"

    // --- JOIN FIELDS (These cause the error if missing) ---
    // Public for easier access in DAOs/Services
    public Student student;
    public Section section;

    // Constructor
    public Enrollment(int enrollmentId, int studentId, int sectionId, String grade) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.grade = grade;
        this.status = "Registered"; // Default status
    }

    // --- GETTERS ---
    public int getEnrollmentId() { return enrollmentId; }
    public int getStudentId() { return studentId; }
    public int getSectionId() { return sectionId; }
    public String getStatus() { return status; }
    public String getGrade() { return grade; }

    // --- SETTERS ---
    public void setGrade(String grade) { this.grade = grade; }
    public void setStatus(String status) { this.status = status; }
}