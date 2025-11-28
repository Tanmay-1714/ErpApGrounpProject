// File: edu.univ.erp.domain.Student.java

package edu.univ.erp.domain;

/**
 * Represents a student profile from the university_erp_db.students table.
 * Extends User to include common authentication data.
 */
public class Student extends User {
    private int studentId; // Primary key of students table
    private String rollNo;
    private String program;
    private int year;

    // Constructor: Calls the User constructor (super) first
    public Student(int userId, String username, String role,
                   int studentId, String rollNo, String program, int year) {
        super(userId, username, role);
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.program = program;
        this.year = year;
    }

    // Getters and Setters specific to Student
    public int getStudentId() { return studentId; }
    public String getRollNo() { return rollNo; }
    public String getProgram() { return program; }
    public int getYear() { return year; }

    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public void setProgram(String program) { this.program = program; }
    public void setYear(int year) { this.year = year; }
}