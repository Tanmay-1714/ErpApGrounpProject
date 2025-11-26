package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for the 'courses' table.
 */
public class CourseDAO {

    /**
     * Inserts a new course into the database.
     * @return true if successful, false otherwise.
     */
    public boolean createCourse(String code, String title, double credits) {
        final String SQL = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setDouble(3, credits);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating course: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all courses. Useful for listing in the UI or for dropdowns.
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        final String SQL = "SELECT * FROM courses ORDER BY code ASC";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getDouble("credits")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error listing courses: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Finds a course by its ID.
     */
    public Course getCourseById(int courseId) {
        final String SQL = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getInt("course_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getDouble("credits")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching course by ID: " + e.getMessage());
        }
        return null;
    }
}
