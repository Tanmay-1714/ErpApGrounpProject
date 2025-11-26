// File: edu.univ.erp.service.AdminService.java (Updated with Section/Assignment Methods)

package edu.univ.erp.service;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.SystemDAO;
import edu.univ.erp.data.UserDAO;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.SectionDAO; // NEW IMPORT REQUIRED

/**
 * Handles all business logic and rule enforcement for Admin actions:
 * Maintenance Mode, User Creation, and Course/Section Management.
 */
public class AdminService {

    private SystemDAO systemDAO;
    private AuthService authService;
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private SectionDAO sectionDAO; // NEW FIELD REQUIRED

    public AdminService() {
        this.systemDAO = new SystemDAO();
        this.authService = new AuthService();
        this.userDAO = new UserDAO();
        this.courseDAO = new CourseDAO();
        this.sectionDAO = new SectionDAO(); // INITIALIZE NEW DAO
    }

    // --- Maintenance Mode Methods ---

    /**
     * Toggles the system's Maintenance Mode flag in the database.
     */
    public String toggleMaintenanceMode(boolean enabled) {
        // Essential Security Check: Only Admins can change this setting
        if (!"Admin".equals(UserSession.getInstance().getRole())) {
            return "FAILURE: Only administrators are authorized to change system settings.";
        }

        // Use the DAO to update the setting
        if (systemDAO.setMaintenanceMode(enabled)) {
            String status = enabled ? "ON" : "OFF";
            return "SUCCESS: Maintenance Mode set to " + status + ".";
        } else {
            return "ERROR: Database update failed. Could not change Maintenance Mode.";
        }
    }

    /**
     * Simple getter to check the current state from the database for UI display.
     */
    public boolean isMaintenanceModeEnabled() {
        return systemDAO.isMaintenanceModeEnabled();
    }

    // --- User Creation Method ---

    /**
     * Complete transaction to create a new user and their associated profile.
     */
    public String createNewUser(String username, String password, String role, String... profileDetails) {
        // Essential Security Check: Only Admins can create users
        if (!"Admin".equals(UserSession.getInstance().getRole())) {
            return "FAILURE: Only administrators are authorized to create new users.";
        }

        // 1. Create Base User (Auth DB)
        int newUserId = authService.createBaseUser(username, password, role);

        if (newUserId == -1) {
            return "FAILURE: User creation in Authentication DB failed (Username may exist or DB error).";
        }

        // 2. Create Profile Record (ERP DB)
        boolean profileSuccess = false;

        if ("Student".equalsIgnoreCase(role) && profileDetails.length >= 3) {
            // profileDetails: [rollNo, program, year]
            try {
                String rollNo = profileDetails[0];
                String program = profileDetails[1];
                int year = Integer.parseInt(profileDetails[2]);
                profileSuccess = userDAO.createStudentProfile(newUserId, rollNo, program, year);
            } catch (NumberFormatException e) {
                profileSuccess = false; // Year was not a number
            }
        } else if ("Instructor".equalsIgnoreCase(role) && profileDetails.length >= 1) {
            // profileDetails: [department]
            String department = profileDetails[0];
            profileSuccess = userDAO.createInstructorProfile(newUserId, department);
        } else {
            // If the role or profile details are invalid
            return "FAILURE: Invalid profile details provided for role: " + role;
        }

        if (profileSuccess) {
            return "SUCCESS: User '" + username + "' created successfully with " + role + " profile. UserID: " + newUserId;
        } else {
            // In a real application, you would ROLLBACK the base user creation here
            // since the profile creation failed. We provide an error message instead.
            return "FAILURE: Profile creation failed. User '" + username + "' created but profile data missing/invalid.";
        }
    }

    // --- Course Management Method (Existing) ---

    /**
     * Creates a new course in the course catalog.
     */
    public String createNewCourse(String code, String title, double credits) {
        // Essential Security Check: Only Admins can create courses
        if (!"Admin".equals(UserSession.getInstance().getRole())) {
            return "FAILURE: Only administrators are authorized to create new courses.";
        }

        // Basic business rule: Check for positive credits
        if (credits <= 0) {
            return "FAILURE: Course must have positive credit hours.";
        }

        if (courseDAO.createCourse(code, title, credits)) {
            return "SUCCESS: Course " + code + " (" + title + ") created successfully.";
        } else {
            return "FAILURE: Course creation failed. Check if course code '" + code + "' already exists.";
        }
    }

    // --- Section Creation Method (NEW) ---

    /**
     * Creates a new course section.
     */
    public String createNewSection(int courseId, String day, String time, String room, int capacity, String semester, int year) {
        // Essential Security Check
        if (!"Admin".equals(UserSession.getInstance().getRole())) {
            return "FAILURE: Only administrators are authorized to create new sections.";
        }

        // Basic business rule: Check capacity
        if (capacity <= 0) {
            return "FAILURE: Section capacity must be greater than zero.";
        }

        // Use the SectionDAO to persist the record
        if (sectionDAO.createSection(courseId, day, time, room, capacity, semester, year)) {
            return "SUCCESS: New section for Course ID " + courseId + " created successfully.";
        } else {
            return "FAILURE: Section creation failed due to a database error or invalid input.";
        }
    }

    // --- Assign Instructor Method (NEW) ---

    /**
     * Assigns an instructor to a section.
     */
    public String assignInstructor(int sectionId, int instructorId) {
        // Essential Security Check
        if (!"Admin".equals(UserSession.getInstance().getRole())) {
            return "FAILURE: Only administrators are authorized to assign instructors.";
        }

        // Use the SectionDAO method to update the instructor_id
        if (sectionDAO.assignInstructorToSection(sectionId, instructorId)) {
            return "SUCCESS: Instructor ID " + instructorId + " assigned to Section ID " + sectionId + ".";
        } else {
            return "FAILURE: Instructor assignment failed. Check if Section ID or Instructor ID are valid.";
        }
    }
}