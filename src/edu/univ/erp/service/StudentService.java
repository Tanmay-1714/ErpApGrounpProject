// File: edu.univ.erp.service.StudentService.java

package edu.univ.erp.service;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Enrollment;

import java.util.List;

public class StudentService {

    private EnrollmentDAO enrollmentDAO;
    private SectionDAO sectionDAO;
    private AdminService adminService;

    public StudentService() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.sectionDAO = new SectionDAO();
        this.adminService = new AdminService();
    }

    // --- 1. Course Catalog ---
    public List<Section> getCourseCatalog() {
        if (!"Student".equals(UserSession.getInstance().getRole())) {
            return List.of();
        }
        return sectionDAO.getAllSections();
    }

    // --- 2. Course Registration (RENAMED to match your UI) ---
    public String registerForSection(int sectionId) {
        if (!"Student".equals(UserSession.getInstance().getRole())) {
            return "FAILURE: Only students can register.";
        }

        // Maintenance Check
        if (adminService.isMaintenanceModeEnabled()) {
            return "FAILURE: Maintenance Mode is ON. Registration is blocked.";
        }

        int studentId = UserSession.getInstance().getProfileId();

        // Duplicate Check
        if (enrollmentDAO.isStudentEnrolled(studentId, sectionId)) {
            return "FAILURE: You are already registered for this section.";
        }

        // Perform Registration
        if (enrollmentDAO.registerStudent(studentId, sectionId)) {
            return "SUCCESS: Registered for Section " + sectionId;
        } else {
            return "FAILURE: Database error during registration.";
        }
    }

    // --- 3. Drop Section (NEW Feature) ---
    public String dropSection(int sectionId) {
        if (!"Student".equals(UserSession.getInstance().getRole())) {
            return "FAILURE: Only students can drop sections.";
        }

        // Maintenance Check
        if (adminService.isMaintenanceModeEnabled()) {
            return "FAILURE: Maintenance Mode is ON. Dropping is blocked.";
        }

        int studentId = UserSession.getInstance().getProfileId();

        if (enrollmentDAO.deleteEnrollment(studentId, sectionId)) {
            return "SUCCESS: Section dropped successfully.";
        } else {
            return "FAILURE: Drop failed. Are you sure you are enrolled?";
        }
    }

    // --- 4. My Courses ---
    public List<Enrollment> getMyRegistrations() {
        if (!"Student".equals(UserSession.getInstance().getRole())) {
            return List.of();
        }
        int studentId = UserSession.getInstance().getProfileId();
        return enrollmentDAO.getMyRegistrations(studentId);
    }
}