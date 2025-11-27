package edu.univ.erp.data;

import edu.univ.erp.domain.User;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Instructor;

import java.sql.*;

/**
 * Handles database operations for User profiles (Student, Instructor, Admin).
 */
public class UserDAO {

    // --- READ METHODS (Used for Login) ---

    public User getUserByUsername(String username) {
        final String SQL = "SELECT user_id, username, role FROM users_auth WHERE username = ?";

        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving base user data: " + e.getMessage());
        }
        return null;
    }

    public Student getStudentProfile(User baseUser) {
        if (!"Student".equals(baseUser.getRole())) return null;

        final String SQL = "SELECT * FROM students WHERE user_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, baseUser.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            baseUser.getUserId(),
                            baseUser.getUsername(),
                            baseUser.getRole(),
                            rs.getInt("user_id"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving Student profile: " + e.getMessage());
        }
        return null;
    }

    public Instructor getInstructorProfile(User baseUser) {
        if (!"Instructor".equals(baseUser.getRole())) return null;

        final String SQL = "SELECT * FROM instructors WHERE user_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, baseUser.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            baseUser.getUserId(),
                            baseUser.getUsername(),
                            baseUser.getRole(),
                            rs.getInt("instructor_id"),
                            rs.getString("department")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving Instructor profile: " + e.getMessage());
        }
        return null;
    }

    /**
     * Admins don't have a separate profile table in this schema,
     * so we just return the base user object.
     */
    public User getAdminProfile(User baseUser) {
        return baseUser;
    }

    // --- WRITE METHODS (Fixes your AdminService Error) ---

    /**
     * Inserts a new row into the 'students' table.
     */
    public boolean createStudentProfile(int userId, String rollNo, String program, int year) {
        final String SQL = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, userId);
            stmt.setString(2, rollNo);
            stmt.setString(3, program);
            stmt.setInt(4, year);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating student profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new row into the 'instructors' table.
     */
    public boolean createInstructorProfile(int userId, String department) {
        final String SQL = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, userId);
            stmt.setString(2, department);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating instructor profile: " + e.getMessage());
            return false;
        }
    }
}
