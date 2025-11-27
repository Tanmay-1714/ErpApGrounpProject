package edu.univ.erp.service;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;

import java.util.List;

public class InstructorService {

    private SectionDAO sectionDAO;
    private EnrollmentDAO enrollmentDAO;
    private AdminService adminService;

    // --- WEIGHTS (Total 1.0) ---
    private static final double WEIGHT_QUIZ = 0.20;
    private static final double WEIGHT_MIDTERM = 0.30;
    private static final double WEIGHT_FINAL = 0.50;

    public InstructorService() {
        this.sectionDAO = new SectionDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.adminService = new AdminService();
    }

    public List<Section> getAssignedSections() {
        if (!"Instructor".equals(UserSession.getInstance().getRole())) return List.of();
        return sectionDAO.getSectionsByInstructorId(UserSession.getInstance().getProfileId());
    }

    public List<Enrollment> getEnrolledStudents(int sectionId) {
        if (!"Instructor".equals(UserSession.getInstance().getRole())) return List.of();
        return enrollmentDAO.getEnrolledStudentsBySection(sectionId);
    }

    // --- Logic for calculating grades ---
    public String updateStudentScore(int enrollmentId, double quiz, double midterm, double fin) {
        if (!"Instructor".equals(UserSession.getInstance().getRole())) return "FAILURE: Unauthorized.";
        if (adminService.isMaintenanceModeEnabled()) return "FAILURE: Maintenance Mode ON.";

        // Calculate Weighted Average
        double totalScore = (quiz * WEIGHT_QUIZ) + (midterm * WEIGHT_MIDTERM) + (fin * WEIGHT_FINAL);

        // Determine Letter/Number Grade
        String grade = calculateGrade(totalScore);

        // Save to DB
        if (enrollmentDAO.updateScoresAndGrade(enrollmentId, quiz, midterm, fin, grade)) {
            return "SUCCESS: Grade Calculated: " + grade;
        }
        return "FAILURE: DB Error.";
    }
    
    // Kept for backward compatibility with older dialogs if needed
    public String submitGrade(int enrollmentId, String grade) {
         if (adminService.isMaintenanceModeEnabled()) return "FAILURE: Maintenance Mode ON.";
         if (enrollmentDAO.updateGrade(enrollmentId, grade)) return "SUCCESS";
         return "FAILURE";
    }

    private String calculateGrade(double score) {
        int g;
        if (score >= 90) g = 10;
        else if (score >= 85) g = 9;
        else if (score >= 75) g = 8;
        else if (score >= 65) g = 7;
        else if (score >= 60) g = 6;
        else if (score >= 50) g = 4; // Explicit Requirement
        else g = 0; // Fail
        return String.valueOf(g);
    }
}
