// File: edu.univ.erp.service.InstructorService.java (COMPLETED SERVICE METHODS)

package edu.univ.erp.service;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Enrollment; // NEW IMPORT: Required for getEnrolledStudents return type

import java.util.List;

/**
 * Handles all business logic and rule enforcement for Instructor actions:
 * Viewing assigned sections, managing enrollments, and submitting grades.
 */
public class InstructorService {

    private SectionDAO sectionDAO;
    private EnrollmentDAO enrollmentDAO;

    public InstructorService() {
        this.sectionDAO = new SectionDAO();
        // EnrollmentDAO is now created and ready for use.
        this.enrollmentDAO = new EnrollmentDAO();
    }

    /**
     * Retrieves all sections currently assigned to the logged-in instructor.
     * Requires the Instructor Profile (instructorId) to be loaded in the session.
     * @return List of Section objects.
     */
    public List<Section> getAssignedSections() {
        // Essential Security/Session Check
        if (!"Instructor".equals(UserSession.getInstance().getRole())) {
            System.err.println("Access denied: Non-instructor trying to access assigned sections.");
            return List.of(); // Return an empty list
        }

        int instructorId = UserSession.getInstance().getProfileId();

        // Assuming SectionDAO.getSectionsByInstructorId(int) is implemented.
        return sectionDAO.getSectionsByInstructorId(instructorId);
    }

    // --- NEW INSTRUCTOR METHODS ---

    /**
     * Retrieves the class roster (students and their current grades) for a given section.
     */
    public List<Enrollment> getEnrolledStudents(int sectionId) {
        if (!"Instructor".equals(UserSession.getInstance().getRole())) {
            System.err.println("Access denied: Non-instructor trying to view roster.");
            return List.of();
        }
        // Assuming EnrollmentDAO.getEnrolledStudentsBySection(int) is implemented.
        return enrollmentDAO.getEnrolledStudentsBySection(sectionId);
    }

    /**
     * Submits a final grade for a student using their enrollment ID.
     */
    public String submitGrade(int enrollmentId, String grade) {
        if (!"Instructor".equals(UserSession.getInstance().getRole())) {
            return "FAILURE: Only instructors can submit grades.";
        }

        String cleanGrade = grade != null ? grade.trim().toUpperCase() : null;

        // Basic Business Rule: Simple validation (adjust as needed, e.g., check against valid list)
        if (cleanGrade == null || cleanGrade.isEmpty() || cleanGrade.length() > 2) {
            return "FAILURE: Invalid grade format submitted. Please use a standard grade (e.g., A, B+, F).";
        }

        // Assuming EnrollmentDAO.updateGrade(int, String) is implemented.
        if (enrollmentDAO.updateGrade(enrollmentId, cleanGrade)) {
            return "SUCCESS: Grade '" + cleanGrade + "' submitted for enrollment ID " + enrollmentId + ".";
        } else {
            return "FAILURE: Grade submission failed due to database error or invalid Enrollment ID.";
        }
    }
}