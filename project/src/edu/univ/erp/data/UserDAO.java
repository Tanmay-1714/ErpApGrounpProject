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
        String sql = "SELECT * FROM students WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, baseUser.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            baseUser.getUserId(),
                            baseUser.getUsername(),
                            baseUser.getRole(),
                            rs.getInt("student_id"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );
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

    // Returns Student ID or -1
    public int createStudentProfile(int userId, String rollNo, String program, int year) {
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getERPDBConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId); stmt.setString(2, rollNo); stmt.setString(3, program); stmt.setInt(4, year);
            int rows = stmt.executeUpdate();
            if(rows > 0) {
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // Returns Instructor ID or -1
    public int createInstructorProfile(int userId, String department) {
        String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
        try (Connection conn = DBConnection.getERPDBConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId); stmt.setString(2, department);
            int rows = stmt.executeUpdate();
            if(rows > 0) {
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}
