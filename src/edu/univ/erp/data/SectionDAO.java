package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public int getSectionCapacity(int sectionId) {
        String sql = "SELECT capacity FROM sections WHERE section_id = ?";
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("capacity");
            }
        } catch (SQLException e) { 
            System.err.println("DEBUG: Failed to get capacity for section " + sectionId);
            e.printStackTrace(); 
        }
        return 0; // Fallback (Closed)
    }

    public List<Section> getAllSections() {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, s.capacity, s.semester, s.year, s.day, s.time, s.room, " +
                "c.course_id, c.code, c.title, c.credits, i.instructor_id " +
                "FROM sections s JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors i ON s.instructor_id = i.instructor_id ORDER BY c.code ASC";
        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course c = new Course(rs.getInt("course_id"), rs.getString("code"), rs.getString("title"), rs.getDouble("credits"));
                Section s = new Section(rs.getInt("section_id"), rs.getInt("course_id"), rs.getInt("instructor_id"), rs.getString("day"), rs.getString("time"), rs.getString("room"), rs.getInt("capacity"), rs.getString("semester"), rs.getInt("year"));
                s.course = c;
                sections.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sections;
    }

    public boolean createSection(int cid, String d, String t, String r, int cap, String s, int y) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day, time, room, capacity, semester, year) VALUES (?, 0, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getERPDBConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cid); stmt.setString(2, d); stmt.setString(3, t); stmt.setString(4, r); stmt.setInt(5, cap); stmt.setString(6, s); stmt.setInt(7, y);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean assignInstructorToSection(int sid, int iid) {
        try (Connection conn = DBConnection.getERPDBConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE sections SET instructor_id = ? WHERE section_id = ?")) {
            stmt.setInt(1, iid); stmt.setInt(2, sid);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Section> getSectionsByInstructorId(int iid) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, s.capacity, s.semester, s.year, s.day, s.time, s.room, c.course_id, c.code, c.title, c.credits FROM sections s JOIN courses c ON s.course_id = c.course_id WHERE s.instructor_id = ?";
        try (Connection conn = DBConnection.getERPDBConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, iid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(rs.getInt("course_id"), rs.getString("code"), rs.getString("title"), rs.getDouble("credits"));
                    Section s = new Section(rs.getInt("section_id"), rs.getInt("course_id"), iid, rs.getString("day"), rs.getString("time"), rs.getString("room"), rs.getInt("capacity"), rs.getString("semester"), rs.getInt("year"));
                    s.course = c;
                    sections.add(s);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sections;
    }
}
