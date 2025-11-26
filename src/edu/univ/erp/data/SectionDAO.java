// File: edu.univ.erp.data.SectionDAO.java (Updated with Admin Write Methods)

package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for Sections and Course Catalogs.
 */
public class SectionDAO {

    /**
     * Retrieves the list of all available sections for the current term/year.
     * This is the Course Catalog view for the student.
     */
    public List<Section> getAllSections() {
        List<Section> sections = new ArrayList<>();
        // Query joins sections, courses, and instructors to get all necessary display info.
        final String SQL = "SELECT s.section_id, s.capacity, s.semester, s.year, " +
                "s.day, s.time, s.room, " + // ADDED: Fetching day, time, room from sections table
                "c.course_id, c.code, c.title, c.credits, " +
                "i.instructor_id, i.department " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                // Note: We use LEFT JOIN here. This allows sections with instructor_id = 0
                // (unassigned) to still appear in the catalog.
                "LEFT JOIN instructors i ON s.instructor_id = i.instructor_id " +
                // Placeholder for filtering (e.g., only show sections for current term)
                "LIMIT 100";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getDouble("credits")
                );

                // Note: The Section constructor is now using the fetched columns.
                Section section = new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("day"),    // ADDED
                        rs.getString("time"),   // ADDED
                        rs.getString("room"),   // ADDED
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year")
                );

                // Attach the Course object for easy UI display
                section.course = course;
                sections.add(section);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving course catalog: " + e.getMessage());
        }
        return sections;
    }

    // --- ADMIN WRITE METHODS ---

    /**
     * Creates a new section record in the ERP database.
     * The instructor_id is initially set to 0 (unassigned) and updated later.
     */
    public boolean createSection(int courseId, String day, String time, String room, int capacity, String semester, int year) {
        // We set instructor_id to 0 for initial creation as per typical requirements.
        final String SQL = "INSERT INTO sections (course_id, instructor_id, day, time, room, capacity, semester, year) " +
                "VALUES (?, 0, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, courseId);
            stmt.setString(2, day);
            stmt.setString(3, time);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);
            stmt.setString(6, semester);
            stmt.setInt(7, year);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating new section: " + e.getMessage());
            return false;
        }
    }

    /**
     * Assigns a specific instructor to an existing section.
     */
    public boolean assignInstructorToSection(int sectionId, int instructorId) {
        final String SQL = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, instructorId);
            stmt.setInt(2, sectionId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning instructor to section: " + e.getMessage());
            return false;
        }
    }

    public List<Section> getSectionsByInstructorId(int instructorId) {
        List<Section> sections = new ArrayList<>();
        // Query joins sections and courses
        final String SQL = "SELECT s.section_id, s.capacity, s.semester, s.year, " +
                "s.day, s.time, s.room, " +
                "c.course_id, c.code, c.title, c.credits " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "WHERE s.instructor_id = ?";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, instructorId);
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
                            instructorId, // We already know the instructor ID
                            rs.getString("day"),
                            rs.getString("time"),
                            rs.getString("room"),
                            rs.getInt("capacity"),
                            rs.getString("semester"),
                            rs.getInt("year")
                    );

                    section.course = course;
                    sections.add(section);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving assigned sections for instructor " + instructorId + ": " + e.getMessage());
        }
        return sections;
    }
}