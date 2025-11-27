package edu.univ.erp.service;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import java.util.List;

public class InstructorService {
    private SectionDAO sectionDAO = new SectionDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private AdminService adminService = new AdminService();

    public List<Section> getAssignedSections() {
        if (!"Instructor".equals(UserSession.getInstance().getRole())) return List.of();
        return sectionDAO.getSectionsByInstructorId(UserSession.getInstance().getProfileId());
    }

    public List<Enrollment> getEnrolledStudents(int sectionId) {
        if (!"Instructor".equals(UserSession.getInstance().getRole())) return List.of();
        return enrollmentDAO.getEnrolledStudentsBySection(sectionId);
    }

    public String updateStudentScore(int enrollmentId, double q, double m, double f) {
        if (!"Instructor".equals(UserSession.getInstance().getRole())) return "FAILURE: Unauthorized.";
        if (adminService.isMaintenanceModeEnabled()) return "FAILURE: Maintenance Mode ON.";

        double total = (q * 0.20) + (m * 0.30) + (f * 0.50);
        String grade;
        
        if (total >= 90) grade = "10";
        else if (total >= 85) grade = "9";
        else if (total >= 75) grade = "8";
        else if (total >= 65) grade = "7";
        else if (total >= 60) grade = "6";
        else if (total >= 50) grade = "4";
        else grade = "0"; // Fail

        if (enrollmentDAO.updateScoresAndGrade(enrollmentId, q, m, f, grade)) return "SUCCESS";
        return "FAILURE: Database Error";
    }
    
    public String submitGrade(int eid, String g) { return updateStudentScore(eid, 0, 0, 0); } // Legacy stub
}
