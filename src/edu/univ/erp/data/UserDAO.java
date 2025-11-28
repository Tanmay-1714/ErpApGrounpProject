package edu.univ.erp.data;

import edu.univ.erp.domain.User;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Instructor;

import java.sql.*;

public class UserDAO {

    public User getUserByUsername(String username) {
        String sql = "SELECT user_id, username, role FROM users_auth WHERE username = ?";
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("role"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Student getStudentProfile(User baseUser) {
        if (!"Student".equals(baseUser.getRole())) return null;
        // FIX: Explicitly match user_id to ensure we get the right student profile
        String sql = "SELECT * FROM students WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, baseUser.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student(
                            baseUser.getUserId(),
                            baseUser.getUsername(),
                            baseUser.getRole(),
                            rs.getInt("student_id"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );
                    System.out.println("DEBUG: Loaded Student Profile. UserID=" + baseUser.getUserId() + ", StudentID=" + s.getStudentId());
                    return s;
                } else {
                    System.err.println("DEBUG: No Student Profile found for UserID=" + baseUser.getUserId());
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Instructor getInstructorProfile(User baseUser) {
        if (!"Instructor".equals(baseUser.getRole())) return null;
        String sql = "SELECT * FROM instructors WHERE user_id = ?";
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, baseUser.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(baseUser.getUserId(), baseUser.getUsername(), baseUser.getRole(),
                            rs.getInt("instructor_id"), rs.getString("department"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User getAdminProfile(User baseUser) { return baseUser; }

    public boolean createStudentProfile(int userId, String rollNo, String program, int year) {
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getERPDBConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId); stmt.setString(2, rollNo); stmt.setString(3, program); stmt.setInt(4, year);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean createInstructorProfile(int userId, String department) {
        String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
        try (Connection conn = DBConnection.getERPDBConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId); stmt.setString(2, department);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
