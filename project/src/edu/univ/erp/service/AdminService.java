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
        if (!"Admin".equals(UserSession.getInstance().getRole())) 
            return "FAILURE: Only administrators are authorized to change system settings.";
        
        boolean success = systemDAO.setMaintenanceMode(enabled);
        return success ? "SUCCESS: Maintenance Mode set to " + (enabled ? "ON" : "OFF") 
                       : "ERROR: Database update failed. Could not change Maintenance Mode.";
    }

    public boolean isMaintenanceModeEnabled() { return systemDAO.isMaintenanceModeEnabled(); }

    public String createNewUser(String u, String p, String r, String... details) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) 
            return "FAILURE: Only administrators are authorized to create new users.";
        
        int uid = authService.createBaseUser(u, p, r);
        if (uid == -1) return "FAILURE: User creation in Authentication DB failed (Username may exist or DB error).";
        
        int profileId = -1;
        
        if ("Student".equalsIgnoreCase(r) && details.length >= 3) {
            try {
                int year = Integer.parseInt(details[2]);
                // VALIDATION: Check for negative/nonsensical year
                if (year < 2000 || year > 2100) {
                     return "FAILURE: Year must be between 2000 and 2100.";
                }
                profileId = userDAO.createStudentProfile(uid, details[0], details[1], year);
                if (profileId > 0) {
                    return "SUCCESS: User '" + u + "' created successfully with Student profile. UserID: " + uid + ", StudentID: " + profileId;
                }
            } catch (NumberFormatException e) {
                return "FAILURE: Invalid year format.";
            }
        } else if ("Instructor".equalsIgnoreCase(r) && details.length >= 1) {
            profileId = userDAO.createInstructorProfile(uid, details[0]);
            if (profileId > 0) {
                 return "SUCCESS: User '" + u + "' created successfully with Instructor profile. UserID: " + uid + ", InstructorID: " + profileId;
            }
        } else {
            return "FAILURE: Invalid profile details provided for role: " + r;
        }
        
        return "FAILURE: Profile creation failed. User '" + u + "' created but profile data missing/invalid.";
    }

    public String createNewCourse(String c, String t, double cr) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) 
            return "FAILURE: Only administrators are authorized to create new courses.";
        
        // VALIDATION: Negative check
        if (cr <= 0) return "FAILURE: Course must have positive credit hours.";
        
        int id = courseDAO.createCourse(c, t, cr);
        return (id > 0) ? "SUCCESS: Course " + id + " (" + c + ") created successfully." 
                        : "FAILURE: Course creation failed. Check if course code '" + c + "' already exists.";
    }

    public String createNewSection(int cid, String d, String t, String r, int cap, String s, int y) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) 
             return "FAILURE: Only administrators are authorized to create new sections.";
        
        // VALIDATION: Negative/Logical checks
        if (cap <= 0) return "FAILURE: Section capacity must be greater than zero.";
        if (y < 2000) return "FAILURE: Invalid year (must be > 2000).";

        int id = sectionDAO.createSection(cid, d, t, r, cap, s, y);
        return (id > 0) ? "SUCCESS: New section for Course ID " + cid + " created successfully. Section ID: " + id
                        : "FAILURE: Section creation failed due to a database error or invalid input.";
    }

    public String assignInstructor(int sid, int iid) {
        if (!"Admin".equals(UserSession.getInstance().getRole())) 
            return "FAILURE: Only administrators are authorized to assign instructors.";
            
        return sectionDAO.assignInstructorToSection(sid, iid) 
                ? "SUCCESS: Instructor ID " + iid + " assigned to Section ID " + sid + "." 
                : "FAILURE: Instructor assignment failed. Check if Section ID or Instructor ID are valid.";
    }

    public List<Enrollment> getAllEnrollments() { return enrollmentDAO.getAllEnrollments(); }
}
