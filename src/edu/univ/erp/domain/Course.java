// File: edu.univ.erp.domain.Course.java

package edu.univ.erp.domain;

/**
 * Represents a course from the university_erp_db.courses table.
 */
public class Course {
    private int courseId;
    private String code;    // e.g., "CS101"
    private String title;   // e.g., "Intro to Programming"
    private double credits;

    // Constructor
    public Course(int courseId, String code, String title, double credits) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public double getCredits() { return credits; }

    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setCode(String code) { this.code = code; }
    public void setTitle(String title) { this.title = title; }
    public void setCredits(double credits) { this.credits = credits; }
}