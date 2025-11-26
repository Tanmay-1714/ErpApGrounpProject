// File: edu.univ.erp.access.AccessService.java

package edu.univ.erp.access;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.SystemDAO;
import edu.univ.erp.domain.User;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;

import javax.swing.JOptionPane; // Used for quick error messages

/**
 * Provides methods to enforce project access rules and check system state.
 */
public class AccessService {

    private SystemDAO systemDAO;

    public AccessService() {
        this.systemDAO = new SystemDAO();
    }

    /**
     * Checks if the system is in Maintenance Mode.
     * If enabled, it shows a warning message and returns true.
     * This check applies ONLY to Students and Instructors.
     * @param actionName A description of the action being attempted.
     * @return true if access is blocked by maintenance mode, false otherwise.
     */
    public boolean checkMaintenanceMode(String actionName) {
        User user = UserSession.getInstance().getCurrentUser();
        if (user == null || "Admin".equals(user.getRole())) {
            // Admins are exempt from Maintenance Mode checks
            return false;
        }

        if (systemDAO.isMaintenanceModeEnabled()) {
            JOptionPane.showMessageDialog(null,
                    "System is currently in Maintenance Mode (View-Only). " + actionName + " is blocked.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * Checks if the currently logged-in user is a Student with a matching roll number.
     * This is a simple, mandatory authorization check for all student-specific actions.
     * @param targetRollNo The roll number associated with the data being manipulated.
     * @return true if the current user is a student whose roll number matches the target.
     */
    public boolean isStudentAuthorized(String targetRollNo) {
        User user = UserSession.getInstance().getCurrentUser();

        if (user instanceof Student) {
            Student student = (Student) user;
            if (student.getRollNo().equals(targetRollNo)) {
                return true;
            }
        }

        JOptionPane.showMessageDialog(null,
                "Access Denied: You are not authorized to modify records for Roll No: " + targetRollNo + ".",
                "Authorization Failure",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * Checks if the currently logged-in user is an Instructor assigned to a specific section.
     * This is the core check for Instructor features (e.g., entering grades).
     * NOTE: This will require a DAO method later to check the database for assignment.
     * @param sectionId The ID of the section being managed.
     * @return true if the instructor is assigned to the section. (Currently returns false as DAO is not yet implemented)
     */
    public boolean isInstructorAssignedToSection(int sectionId) {
        // --- TODO: IMPLEMENT DATABASE LOOKUP HERE ---
        // For now, we only check if the user is an instructor.
        User user = UserSession.getInstance().getCurrentUser();
        if (user instanceof Instructor) {
            // Placeholder: Assume Instructor is authorized for now until we build the DAO check.
            // In the final version, you'd use a SectionDAO here to verify assignment.
            // Example: return sectionDAO.isInstructorAssigned(instructor.getInstructorId(), sectionId);
            return true;
        }

        JOptionPane.showMessageDialog(null,
                "Access Denied: You are not authorized to manage Section ID: " + sectionId + ".",
                "Authorization Failure",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * Checks if the current user is an Admin.
     */
    public boolean isAdmin() {
        return "Admin".equals(UserSession.getInstance().getRole());
    }
}
