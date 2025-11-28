package edu.univ.erp.auth;

import edu.univ.erp.data.DBConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.time.LocalDateTime;

public class AuthService {
    // Security Constants
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 5;

    /**
     * Authenticates user with Lockout Logic.
     * @return "LOCKED" if account is locked, Role if success, null if failed.
     */
    public String authenticate(String username, String password) {
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT password_hash, role, failed_attempts, lockout_time FROM users_auth WHERE username = ?")) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null; // User not found

                String hash = rs.getString("password_hash");
                String role = rs.getString("role");
                int attempts = rs.getInt("failed_attempts");
                Timestamp lockoutTime = rs.getTimestamp("lockout_time");

                // 1. CHECK LOCKOUT STATUS
                if (lockoutTime != null) {
                    if (lockoutTime.toLocalDateTime().isAfter(LocalDateTime.now())) {
                        return "LOCKED"; // Still blocked
                    } else {
                        // Time has passed, auto-unlock
                        resetLockout(username); 
                    }
                }

                // 2. VERIFY PASSWORD
                if (BCrypt.checkpw(password, hash)) {
                    resetLockout(username); // Success! Reset counters.
                    return role;
                } else {
                    handleFailedLogin(username, attempts); // Fail! Increment counter.
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleFailedLogin(String username, int currentAttempts) {
        String sql = "UPDATE users_auth SET failed_attempts = ?, lockout_time = ? WHERE username = ?";
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int newAttempts = currentAttempts + 1;
            Timestamp newLockout = null;
            
            // If we hit the limit, set the lockout time to NOW + 5 MINUTES
            if (newAttempts >= MAX_ATTEMPTS) {
                newLockout = Timestamp.valueOf(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
            }

            stmt.setInt(1, newAttempts);
            stmt.setTimestamp(2, newLockout);
            stmt.setString(3, username);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void resetLockout(String username) {
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users_auth SET failed_attempts = 0, lockout_time = NULL WHERE username = ?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // (Keep createBaseUser and changePassword as they were in previous versions)
    public int createBaseUser(String username, String password, String role) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)", 
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username); stmt.setString(2, role); stmt.setString(3, hash);
            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public boolean changePassword(String username, String newPass) {
        String hash = BCrypt.hashpw(newPass, BCrypt.gensalt());
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users_auth SET password_hash = ? WHERE username = ?")) {
            stmt.setString(1, hash); stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}
