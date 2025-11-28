package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public int createCourse(String code, String title, double credits) {
        final String SQL = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setDouble(3, credits);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the new Course ID
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating course: " + e.getMessage());
        }
        return -1; // Failure
    }

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
