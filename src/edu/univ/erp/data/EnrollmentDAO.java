package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    // --- STUDENT: Check Enrollment ---
    public boolean isStudentEnrolled(int studentId, int sectionId) {
        final String SQL = "SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'Registered'";
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { return false; }
    }

    // --- STUDENT: Register ---
    public boolean registerStudent(int studentId, int sectionId) {
        final String SQL = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'Registered')";
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- STUDENT: Drop ---
    public boolean deleteEnrollment(int studentId, int sectionId) {
        final String SQL = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- STUDENT: View My Courses ---
    public List<Enrollment> getMyRegistrations(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        final String SQL = "SELECT e.enrollment_id, e.section_id, e.grade, " +
                "s.semester, s.year, s.day, s.time, s.room, " +
                "c.code, c.title, c.credits, c.course_id " +
                "FROM enrollments e " +
                "LEFT JOIN sections s ON e.section_id = s.section_id " +
                "LEFT JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? AND e.status = 'Registered' " +
                "ORDER BY s.year DESC, s.semester DESC";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(rs.getInt("course_id"), rs.getString("code"), rs.getString("title"), rs.getDouble("credits"));
                    Section s = new Section(rs.getInt("section_id"), rs.getInt("course_id"), -1, rs.getString("day"), rs.getString("time"), rs.getString("room"), -1, rs.getString("semester"), rs.getInt("year"));
                    s.course = c;
                    Enrollment e = new Enrollment(rs.getInt("enrollment_id"), studentId, rs.getInt("section_id"), rs.getString("grade"));
                    e.section = s;
                    enrollments.add(e);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return enrollments;
    }

    // --- INSTRUCTOR: View Roster with Scores ---
    public List<Enrollment> getEnrolledStudentsBySection(int sectionId) {
        List<Enrollment> enrollments = new ArrayList<>();
        // Fetch scores and use LEFT JOIN for robustness
        final String SQL = "SELECT e.enrollment_id, e.grade, e.score_quiz, e.score_midterm, e.score_final, " +
                "e.student_id, u.user_id, u.username, s.roll_no, s.program, s.year " +
                "FROM enrollments e " +
                "LEFT JOIN students s ON e.student_id = s.student_id " +
                "LEFT JOIN university_auth_db.users_auth u ON s.user_id = u.user_id " +
                "WHERE e.section_id = ? AND e.status = 'Registered'";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    if (username == null) username = "Unknown";
                    
                    Student s = new Student(rs.getInt("user_id"), username, "Student", rs.getInt("student_id"), rs.getString("roll_no"), rs.getString("program"), rs.getInt("year"));
                    
                    Enrollment e = new Enrollment(rs.getInt("enrollment_id"), rs.getInt("student_id"), sectionId, rs.getString("grade"));
                    // Load scores
                    e.setScores(rs.getDouble("score_quiz"), rs.getDouble("score_midterm"), rs.getDouble("score_final"));
                    e.student = s;
                    enrollments.add(e);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return enrollments;
    }

    // --- INSTRUCTOR: Update Scores and Grade ---
    public boolean updateScoresAndGrade(int enrollmentId, double quiz, double midterm, double fin, String grade) {
        final String SQL = "UPDATE enrollments SET score_quiz=?, score_midterm=?, score_final=?, grade=? WHERE enrollment_id=?";
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setDouble(1, quiz);
            stmt.setDouble(2, midterm);
            stmt.setDouble(3, fin);
            stmt.setString(4, grade);
            stmt.setInt(5, enrollmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }
    
    // Legacy method for backward compatibility if needed, though updated version is better
    public boolean updateGrade(int enrollmentId, String grade) {
        return updateScoresAndGrade(enrollmentId, 0, 0, 0, grade);
    }

    // --- ADMIN: View All Enrollments ---
    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        final String SQL = "SELECT e.enrollment_id, e.grade, e.status, e.student_id, e.section_id, " +
                "st.roll_no, u.username, c.code " +
                "FROM enrollments e " +
                "LEFT JOIN students st ON e.student_id = st.student_id " +
                "LEFT JOIN university_auth_db.users_auth u ON st.user_id = u.user_id " +
                "LEFT JOIN sections s ON e.section_id = s.section_id " +
                "LEFT JOIN courses c ON s.course_id = c.course_id " +
                "ORDER BY e.enrollment_id DESC LIMIT 500";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                if (username == null) username = "Unknown User"; // Fix nulls
                
                String rollNo = rs.getString("roll_no");
                if (rollNo == null) rollNo = "N/A";

                String courseCode = rs.getString("code");
                if (courseCode == null) courseCode = "Unknown";

                Student s = new Student(0, username, "Student", rs.getInt("student_id"), rollNo, "", 0);
                Course c = new Course(0, courseCode, "", 0);
                Section sec = new Section(rs.getInt("section_id"), 0, 0, "", "", "", 0, "", 0);
                sec.course = c;

                Enrollment e = new Enrollment(rs.getInt("enrollment_id"), rs.getInt("student_id"), rs.getInt("section_id"), rs.getString("grade"));
                e.setStatus(rs.getString("status"));
                e.student = s;
                e.section = sec;
                list.add(e);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}
