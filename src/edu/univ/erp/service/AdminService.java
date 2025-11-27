package edu.univ.erp.service;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Enrollment;
import java.util.List;

public class AdminService {
    private SystemDAO systemDAO = new SystemDAO();
    private AuthService authService = new AuthService();
    private UserDAO userDAO = new UserDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public String toggleMaintenanceMode(boolean enabled) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) return "FAILURE";
        return systemDAO.setMaintenanceMode(enabled) ? "SUCCESS: Maintenance " + (enabled ? "ON" : "OFF") : "ERROR";
    }

    public boolean isMaintenanceModeEnabled() { return systemDAO.isMaintenanceModeEnabled(); }

    public String createNewUser(String u, String p, String r, String... details) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) return "FAILURE";
        int uid = authService.createBaseUser(u, p, r);
        if (uid == -1) return "FAILURE: User exists.";
        
        boolean ok = false;
        if ("Student".equalsIgnoreCase(r) && details.length >= 3) {
            try { ok = userDAO.createStudentProfile(uid, details[0], details[1], Integer.parseInt(details[2])); } 
            catch (Exception e) {}
        } else if ("Instructor".equalsIgnoreCase(r) && details.length >= 1) {
            ok = userDAO.createInstructorProfile(uid, details[0]);
        }
        return ok ? "SUCCESS" : "FAILURE: Profile Error";
    }

    public String createNewCourse(String c, String t, double cr) {
        return courseDAO.createCourse(c, t, cr) ? "SUCCESS" : "FAILURE";
    }

    public String createNewSection(int cid, String d, String t, String r, int cap, String s, int y) {
        return sectionDAO.createSection(cid, d, t, r, cap, s, y) ? "SUCCESS" : "FAILURE";
    }

    public String assignInstructor(int sid, int iid) {
        return sectionDAO.assignInstructorToSection(sid, iid) ? "SUCCESS" : "FAILURE";
    }

    public List<Enrollment> getAllEnrollments() { return enrollmentDAO.getAllEnrollments(); }
}
