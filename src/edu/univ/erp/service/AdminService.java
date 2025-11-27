package edu.univ.erp.service;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Enrollment;

import java.util.List;

public class AdminService {

    private SystemDAO systemDAO;
    private AuthService authService;
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private SectionDAO sectionDAO;
    private EnrollmentDAO enrollmentDAO;

    public AdminService() {
        this.systemDAO = new SystemDAO();
        this.authService = new AuthService();
        this.userDAO = new UserDAO();
        this.courseDAO = new CourseDAO();
        this.sectionDAO = new SectionDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    public String toggleMaintenanceMode(boolean enabled) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) return "FAILURE: Unauthorized.";
        if (systemDAO.setMaintenanceMode(enabled)) {
            return "SUCCESS: Maintenance Mode set to " + (enabled ? "ON" : "OFF") + ".";
        }
        return "ERROR: Database update failed.";
    }

    public boolean isMaintenanceModeEnabled() {
        return systemDAO.isMaintenanceModeEnabled();
    }

    public String createNewUser(String username, String password, String role, String... profileDetails) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) return "FAILURE: Unauthorized.";

        int newUserId = authService.createBaseUser(username, password, role);
        if (newUserId == -1) return "FAILURE: User creation failed (Username exists?).";

        boolean success = false;
        if ("Student".equalsIgnoreCase(role) && profileDetails.length >= 3) {
            try {
                int year = Integer.parseInt(profileDetails[2]);
                success = userDAO.createStudentProfile(newUserId, profileDetails[0], profileDetails[1], year);
            } catch (NumberFormatException e) { return "FAILURE: Invalid Year."; }
        } else if ("Instructor".equalsIgnoreCase(role) && profileDetails.length >= 1) {
            success = userDAO.createInstructorProfile(newUserId, profileDetails[0]);
        } else {
            return "FAILURE: Invalid role details.";
        }
        return success ? "SUCCESS: User created." : "FAILURE: Profile creation failed.";
    }

    public String createNewCourse(String code, String title, double credits) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) return "FAILURE: Unauthorized.";
        if (courseDAO.createCourse(code, title, credits)) return "SUCCESS: Course created.";
        return "FAILURE: Course creation failed.";
    }

    public String createNewSection(int courseId, String day, String time, String room, int capacity, String semester, int year) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) return "FAILURE: Unauthorized.";
        if (sectionDAO.createSection(courseId, day, time, room, capacity, semester, year)) return "SUCCESS: Section created.";
        return "FAILURE: Section creation failed.";
    }

    public String assignInstructor(int sectionId, int instructorId) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) return "FAILURE: Unauthorized.";
        if (sectionDAO.assignInstructorToSection(sectionId, instructorId)) return "SUCCESS: Instructor assigned.";
        return "FAILURE: Assignment failed.";
    }

    public List<Enrollment> getAllEnrollments() {
        if (!"Admin".equals(UserSession.getInstance().getRole())) return List.of();
        return enrollmentDAO.getAllEnrollments();
    }
}
