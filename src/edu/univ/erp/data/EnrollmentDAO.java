package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public int getEnrollmentCount(int sectionId) {
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM enrollments WHERE section_id = ? AND status = 'Registered'")) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean isStudentEnrolled(int studentId, int sectionId) {
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'Registered'")) {
            stmt.setInt(1, studentId); stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean registerStudent(int studentId, int sectionId) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status, score_quiz, score_midterm, score_final) VALUES (?, ?, 'Registered', 0, 0, 0)";
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            int rows = stmt.executeUpdate();
            System.out.println("DEBUG: Registering Student " + studentId + " to Section " + sectionId + " -> Result: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("DEBUG: Registration Failed SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false; 
        }
    }

    public boolean deleteEnrollment(int studentId, int sectionId) {
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM enrollments WHERE student_id = ? AND section_id = ?")) {
            stmt.setInt(1, studentId); stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Enrollment> getMyRegistrations(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        // FIX: Use alias 'cid' to avoid ambiguity if multiple columns are named course_id
        String sql = "SELECT e.enrollment_id, e.section_id, e.grade, " +
                "s.semester, s.year, s.day, s.time, s.room, " +
                "c.code, c.title, c.credits, c.course_id AS cid " +
                "FROM enrollments e " +
                "LEFT JOIN sections s ON e.section_id = s.section_id " +
                "LEFT JOIN courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? AND e.status = 'Registered' " +
                "ORDER BY s.year DESC, s.semester DESC";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            System.out.println("DEBUG: Fetching registrations for Student ID: " + studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(rs.getInt("cid"), rs.getString("code"), rs.getString("title"), rs.getDouble("credits"));
                    Section s = new Section(rs.getInt("section_id"), rs.getInt("cid"), -1, rs.getString("day"), rs.getString("time"), rs.getString("room"), -1, rs.getString("semester"), rs.getInt("year"));
                    s.course = c;
                    Enrollment e = new Enrollment(rs.getInt("enrollment_id"), studentId, rs.getInt("section_id"), rs.getString("grade"));
                    e.section = s;
                    enrollments.add(e);
                    System.out.println("DEBUG: Found enrollment for " + c.getCode());
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return enrollments;
    }

    public List<Enrollment> getEnrolledStudentsBySection(int sectionId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.grade, e.score_quiz, e.score_midterm, e.score_final, e.student_id, u.user_id, u.username, s.roll_no, s.program, s.year " +
                     "FROM enrollments e LEFT JOIN students s ON e.student_id=s.student_id LEFT JOIN university_auth_db.users_auth u ON s.user_id=u.user_id " +
                     "WHERE e.section_id=? AND e.status='Registered'";
        try (Connection conn = DBConnection.getERPDBConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(rs.getInt("user_id"), rs.getString("username"), "Student", rs.getInt("student_id"), rs.getString("roll_no"), rs.getString("program"), rs.getInt("year"));
                    Enrollment e = new Enrollment(rs.getInt("enrollment_id"), rs.getInt("student_id"), sectionId, rs.getString("grade"));
                    e.setScores(rs.getDouble("score_quiz"), rs.getDouble("score_midterm"), rs.getDouble("score_final"));
                    e.student = s;
                    enrollments.add(e);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return enrollments;
    }

    public boolean updateScoresAndGrade(int eid, double q, double m, double f, String g) {
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE enrollments SET score_quiz=?, score_midterm=?, score_final=?, grade=? WHERE enrollment_id=?")) {
            stmt.setDouble(1, q); stmt.setDouble(2, m); stmt.setDouble(3, f); stmt.setString(4, g); stmt.setInt(5, eid);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.grade, e.status, e.student_id, e.section_id, st.roll_no, u.username, s.section_id, c.code " +
                     "FROM enrollments e LEFT JOIN students st ON e.student_id=st.student_id LEFT JOIN university_auth_db.users_auth u ON st.user_id=u.user_id " +
                     "LEFT JOIN sections s ON e.section_id=s.section_id LEFT JOIN courses c ON s.course_id=c.course_id " +
                     "ORDER BY e.enrollment_id DESC LIMIT 500";
        try (Connection conn = DBConnection.getERPDBConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String u = rs.getString("username"); if(u==null) u="Unknown";
                Student s = new Student(0, u, "Student", rs.getInt("student_id"), rs.getString("roll_no"), "", 0);
                Course c = new Course(0, rs.getString("code")!=null?rs.getString("code"):"?", "", 0);
                Section sec = new Section(rs.getInt("section_id"), 0, 0, "", "", "", 0, "", 0);
                sec.course = c;
                Enrollment e = new Enrollment(rs.getInt("enrollment_id"), rs.getInt("student_id"), rs.getInt("section_id"), rs.getString("grade"));
                e.setStatus(rs.getString("status"));
                e.student = s; e.section = sec;
                list.add(e);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
