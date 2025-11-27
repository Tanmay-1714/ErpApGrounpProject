package edu.univ.erp.auth;

import edu.univ.erp.data.DBConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.time.LocalDateTime;

public class AuthService {
    private static final int MAX_ATTEMPTS = 5;

    public String authenticate(String username, String password) {
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT password_hash, role, failed_attempts, lockout_time FROM users_auth WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                
                Timestamp lockout = rs.getTimestamp("lockout_time");
                if (lockout != null && lockout.toLocalDateTime().isAfter(LocalDateTime.now())) return "LOCKED";

                if (BCrypt.checkpw(password, rs.getString("password_hash"))) {
                    resetLockout(username);
                    return rs.getString("role");
                } else {
                    handleFailedLogin(username, rs.getInt("failed_attempts"));
                    return null;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    private void handleFailedLogin(String username, int attempts) {
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users_auth SET failed_attempts = ?, lockout_time = ? WHERE username = ?")) {
            int newAttempts = attempts + 1;
            stmt.setInt(1, newAttempts);
            stmt.setTimestamp(2, newAttempts >= MAX_ATTEMPTS ? Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)) : null);
            stmt.setString(3, username);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void resetLockout(String username) {
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users_auth SET failed_attempts = 0, lockout_time = NULL WHERE username = ?")) {
            stmt.setString(1, username); stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean changePassword(String username, String newPass) {
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users_auth SET password_hash = ? WHERE username = ?")) {
            stmt.setString(1, BCrypt.hashpw(newPass, BCrypt.gensalt()));
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public int createBaseUser(String username, String password, String role) {
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username); stmt.setString(2, role); stmt.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}
