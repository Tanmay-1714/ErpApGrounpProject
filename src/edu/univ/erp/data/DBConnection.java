package edu.univ.erp.data;

// File: edu.univ.erp.data.DBConnection.java

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to provide connection objects for the two databases.
 */
public class DBConnection {

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    // !!! IMPORTANT: Replace this with the actual password you set !!!
    private static final String PASSWORD = "project";

    // Method to get a connection to the AUTH DB
    public static Connection getAuthDBConnection() throws SQLException {
        // We connect directly to the specific database
        return DriverManager.getConnection(URL + "university_auth_db", USER, PASSWORD);
    }

    // Method to get a connection to the ERP DB
    public static Connection getERPDBConnection() throws SQLException {
        return DriverManager.getConnection(URL + "university_erp_db", USER, PASSWORD);
    }

    // Test the connection
    public static void main(String[] args) {
        System.out.println("Testing Database Connections...");
        try (Connection conn = getAuthDBConnection()) {
            System.out.println("✅ Success: university_auth_db is reachable!");
        } catch (SQLException e) {
            System.err.println("❌ Auth DB Connection FAILED! Check password, port, and server status.");
            e.printStackTrace();
        }

        try (Connection conn = getERPDBConnection()) {
            System.out.println("✅ Success: university_erp_db is reachable!");
        } catch (SQLException e) {
            System.err.println("❌ ERP DB Connection FAILED! Check password, port, and server status.");
            e.printStackTrace();
        }
    }
}
