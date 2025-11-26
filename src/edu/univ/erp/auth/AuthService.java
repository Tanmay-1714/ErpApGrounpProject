package edu.univ.erp.auth;

// File: edu.univ.erp.auth.AuthService.java

import edu.univ.erp.data.DBConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class AuthService {

    /**
     * Attempts to log in a user.
     * @return The user's role (Admin, Instructor, Student) or null if login fails.
     */
    public String authenticate(String username, String password) {
        // 1. Get a connection to the Auth DB
        try (Connection conn = DBConnection.getAuthDBConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT password_hash, role FROM users_auth WHERE username = ?")) {

            // 2. Query the hash and role for the given username
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    // User not found
                    return null;
                }

                String storedHash = rs.getString("password_hash");
                String role = rs.getString("role");

                // 3. Verify the input password against the stored hash using BCrypt
                if (BCrypt.checkpw(password, storedHash)) {
                    return role; // Login successful!
                } else {
                    return null; // Password mismatch
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
            return null;
        }
    }

    // --- THE MISSING METHOD FIXING YOUR ERROR ---

    /**
     * Creates a new user record in the Authentication Database.
     * @param username The new username.
     * @param password The raw password (will be hashed).
     * @param role The user's role (Student, Instructor, Admin).
     * @return The generated user_id, or -1 if creation failed.
     */
    public int createBaseUser(String username, String password, String role) {
        // 1. Hash the password
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        // 2. Prepare SQL to insert into Auth DB
        final String SQL = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getAuthDBConnection();
             // RETURN_GENERATED_KEYS allows us to get the new auto-increment ID back
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setString(3, passwordHash);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return -1; // Creating user failed, no rows affected
            }

            // 3. Retrieve the new user_id to link with the Profile DB later
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    return -1; // Creating user failed, no ID obtained
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating base user: " + e.getMessage());
            return -1;
        }
    }
}