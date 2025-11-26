// File: edu.univ.erp.domain.Instructor.java

package edu.univ.erp.domain;

/**
 * Represents an instructor profile from the university_erp_db.instructors table.
 * Extends User to include common authentication data.
 */
public class Instructor extends User {
    private int instructorId; // Primary key of instructors table
    private String department;

    // Constructor: Calls the User constructor (super) first
    public Instructor(int userId, String username, String role,
                      int instructorId, String department) {
        super(userId, username, role);
        this.instructorId = instructorId;
        this.department = department;
    }

    // Getters and Setters specific to Instructor
    public int getInstructorId() { return instructorId; }
    public String getDepartment() { return department; }

    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public void setDepartment(String department) { this.department = department; }
}