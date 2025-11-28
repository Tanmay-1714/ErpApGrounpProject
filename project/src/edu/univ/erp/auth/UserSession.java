package edu.univ.erp.auth;

import edu.univ.erp.domain.User;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Instructor;

/**
 * Singleton class to manage the session state of the currently logged-in user.
 */
public class UserSession {

    private static UserSession instance;
    private User currentUserProfile;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User userProfile) {
        this.currentUserProfile = userProfile;
    }

    public User getCurrentUser() {
        return currentUserProfile;
    }

    public void clearSession() {
        this.currentUserProfile = null;
    }

    public boolean isLoggedIn() {
        return currentUserProfile != null;
    }

    public String getRole() {
        return currentUserProfile != null ? currentUserProfile.getRole() : null;
    }

    public String getUsername() {
        return currentUserProfile != null ? currentUserProfile.getUsername() : null;
    }

    // --- THE MISSING METHOD ---
    /**
     * Retrieves the role-specific ID (Student ID or Instructor ID) based on the current user type.
     * @return The Student ID or Instructor ID, or -1 if the role is Admin or unknown.
     */
    public int getProfileId() {
        if (currentUserProfile instanceof Student) {
            return ((Student) currentUserProfile).getStudentId();
        } else if (currentUserProfile instanceof Instructor) {
            return ((Instructor) currentUserProfile).getInstructorId();
        }
        // For Admin or base User, return -1 (or handle differently if needed)
        return -1;
    }
}