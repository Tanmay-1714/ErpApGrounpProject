// File: edu.univ.erp.data.SystemDAO.java

package edu.univ.erp.data;

import java.sql.*;

/**
 * Handles database operations for system-wide settings, like Maintenance Mode.
 */
public class SystemDAO {

    /**
     * Retrieves the current state of Maintenance Mode from the settings table.
     * @return true if Maintenance Mode is ON, false otherwise.
     */
    public boolean isMaintenanceModeEnabled() {
        // We assume a 'settings' table exists in the ERP DB with a key 'MAINTENANCE_MODE'
        // and a value of 'true' or 'false'.
        final String SQL = "SELECT value FROM settings WHERE key_name = 'MAINTENANCE_MODE'";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // The value is stored as a string, so we convert it to a boolean
                return "true".equalsIgnoreCase(rs.getString("value"));
            }
        } catch (SQLException e) {
            System.err.println("Error reading Maintenance Mode flag: " + e.getMessage());
            // Fail-safe: if the database is down or table is missing, assume mode is off.
        }
        return false;
    }

    public boolean setMaintenanceMode(boolean enabled) {
        // The value is stored as the string 'true' or 'false'
        final String newValue = enabled ? "true" : "false";
        final String SQL = "UPDATE settings SET value = ? WHERE key_name = 'MAINTENANCE_MODE'";

        try (Connection conn = DBConnection.getERPDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, newValue);
            // Check if one row was affected (i.e., the update worked)
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating Maintenance Mode flag: " + e.getMessage());
            return false;
        }
    }
}