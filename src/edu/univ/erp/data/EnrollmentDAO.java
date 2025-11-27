package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for enrollments.
 * Contains methods for Student, Instructor, and Admin features.
 */
public class EnrollmentDAO {

    // =============================================================
    //                     STUDENT FEATURES
    // =============================================================

    /**
     * Checks if a student is already enrolled in a specific section.
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

    /**
     * Registers a student into a section.
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

    /**
     * Drops (deletes) an enrollment for a student.
     */
    public boolean deleteEnrollment(int studentId, int sectionId) {
        final String SQL = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error dropping section: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all courses a student has registered for.
     */
    public List<Enrollment> getMyRegistrations(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        final String SQL = "SELECT e.enrollment_id, e.section_id, e.grade, " +
                "s.semester, s.year, s.day, s.time, s.room, " +
                "c.code, c.title, c.credits, c.course_id " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? AND e.status = 'Registered' " +
                "ORDER BY s.year DESC, s.semester DESC";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("course_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getDouble("credits")
                    );

                    Section section = new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            -1,
                            rs.getString("day"),
                            rs.getString("time"),
                            rs.getString("room"),
                            -1,
                            rs.getString("semester"),
                            rs.getInt("year")
                    );
                    section.course = course;

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
            System.err.println("Error retrieving student registrations: " + e.getMessage());
        }
        return enrollments;
    }

    // =============================================================
    //                     INSTRUCTOR FEATURES
    // =============================================================

    /**
     * Retrieves the list of students in a specific section.
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
                    Student student = new Student(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            "Student",
                            rs.getInt("student_id"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );

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
            System.err.println("Error retrieving roster: " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Updates the grade for an enrollment.
     */
    public boolean updateGrade(int enrollmentId, String grade) {
        final String SQL = "UPDATE enrollments SET grade = ? WHERE enrollment_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, grade);
            stmt.setInt(2, enrollmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating grade: " + e.getMessage());
            return false;
        }
    }

    // =============================================================
    //                     ADMIN FEATURES
    // =============================================================

    /**
     * Retrieves ALL enrollments in the system (for Admin View).
     */
    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        final String SQL = "SELECT e.enrollment_id, e.grade, e.status, " +
                "st.student_id, st.roll_no, u.username, " +
                "s.section_id, c.code " +
                "FROM enrollments e " +
                "JOIN students st ON e.student_id = st.student_id " +
                "JOIN users_auth u ON st.user_id = u.user_id " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "ORDER BY e.enrollment_id DESC LIMIT 500";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Construct Student Stub
                Student student = new Student(
                        0, rs.getString("username"), "Student",
                        rs.getInt("student_id"), rs.getString("roll_no"), "", 0
                );

                // Construct Course/Section Stub
                Course course = new Course(0, rs.getString("code"), "", 0);
                Section section = new Section(
                        rs.getInt("section_id"), 0, 0, "", "", "", 0, "", 0
                );
                section.course = course;

                // Construct Enrollment
                Enrollment enrollment = new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("grade")
                );
                enrollment.setStatus(rs.getString("status"));
                enrollment.student = student;
                enrollment.section = section;

                list.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("Error listing all enrollments: " + e.getMessage());
        }
        return list;
    }
}
