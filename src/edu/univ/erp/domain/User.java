// File: edu.univ.erp.domain.User.java

package edu.univ.erp.domain;

/**
 * Represents a user from the university_auth_db. 
 * This is the common data model for Admin, Instructor, and Student.
 */
public class User {
    private int userId;
    private String username;
    private String role; // "Admin", "Instructor", "Student"

    // Constructor
    public User(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // Getters and Setters (essential for accessing data)
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
}