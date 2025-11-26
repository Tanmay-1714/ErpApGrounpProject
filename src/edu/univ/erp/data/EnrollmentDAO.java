// File: edu.univ.erp.data.EnrollmentDAO.java (FINAL VERSION)

package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Section; // NEW IMPORT REQUIRED for getMyRegistrations
import edu.univ.erp.domain.Course;  // NEW IMPORT REQUIRED for getMyRegistrations
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for enrollments (Register, Drop, Check, Roster, Grade, MyCourses).
 */
public class EnrollmentDAO {

    /**
     * Checks if a student is already enrolled (status='Registered') in a specific section.
     */
    public boolean isStudentEnrolled(int studentId, int sectionId) {
        final String SQL = "SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'Registered'";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking enrollment status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEnrollment(int studentId, int sectionId) {
        final String SQL = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);

            // executeUpdate returns the number of rows affected. > 0 means success.
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error dropping section: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registers a student into a section.
     * @return true on success, false on database error.
     */
    public boolean registerStudent(int studentId, int sectionId) {
        final String SQL = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'Registered')";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error during student registration: " + e.getMessage());
            return false;
        }
    }

    // --- INSTRUCTOR FUNCTIONALITY ---

    /**
     * Retrieves the list of students enrolled in a specific section (class roster).
     */
    public List<Enrollment> getEnrolledStudentsBySection(int sectionId) {
        List<Enrollment> enrollments = new ArrayList<>();

        final String SQL = "SELECT e.enrollment_id, e.grade, e.student_id, " +
                "u.user_id, u.username, " +
                "s.roll_no, s.program, s.year " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN users_auth u ON s.user_id = u.user_id " +
                "WHERE e.section_id = ? AND e.status = 'Registered'";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Create the Student object
                    Student student = new Student(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            "Student",
                            rs.getInt("student_id"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );

                    // 2. Create the Enrollment record
                    Enrollment enrollment = new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            sectionId,
                            rs.getString("grade")
                    );

                    enrollment.student = student;
                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving enrolled students for section " + sectionId + ": " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Updates the final grade for a specific enrollment record.
     */
    public boolean updateGrade(int enrollmentId, String grade) {
        final String SQL = "UPDATE enrollments SET grade = ? WHERE enrollment_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, grade);
            stmt.setInt(2, enrollmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating grade for enrollment ID " + enrollmentId + ": " + e.getMessage());
            return false;
        }
    }

    // --- STUDENT FUNCTIONALITY (NEW) ---

    /**
     * Retrieves all enrollment records (including course info) for a specific student.
     * Used by the Student Dashboard to view registered courses and final grades.
     */
    public List<Enrollment> getMyRegistrations(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        // Joins enrollments with sections and courses to get all display info
        final String SQL = "SELECT e.enrollment_id, e.section_id, e.grade, " +
                "s.semester, s.year, s.day, s.time, s.room, " +
                "c.code, c.title, c.credits, c.course_id " + // Added course_id
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? " +
                "AND e.status = 'Registered' " +
                "ORDER BY s.year DESC, s.semester DESC";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create Course object
                    Course course = new Course(
                            rs.getInt("course_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getDouble("credits")
                    );

                    // Create Section object
                    Section section = new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            -1, // instructorId is not necessary here
                            rs.getString("day"),
                            rs.getString("time"),
                            rs.getString("room"),
                            -1, // capacity is not necessary here
                            rs.getString("semester"),
                            rs.getInt("year")
                    );
                    section.course = course; // Attach the course info

                    // Create the Enrollment record
                    Enrollment enrollment = new Enrollment(
                            rs.getInt("enrollment_id"),
                            studentId,
                            rs.getInt("section_id"),
                            rs.getString("grade")
                    );

                    enrollment.section = section;
                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student registrations for ID " + studentId + ": " + e.getMessage());
        }
        return enrollments;
    }
}