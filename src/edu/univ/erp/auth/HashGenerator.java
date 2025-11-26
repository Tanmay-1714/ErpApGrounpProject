package edu.univ.erp.auth;

// File: edu.univ.erp.auth.HashGenerator.java

import org.mindrot.jbcrypt.BCrypt;

/**
 * A temporary utility to generate secure BCrypt hashes for initial users.
 * NOTE: BCrypt is available because you added the jbcrypt-0.4.jar dependency.
 */
public class HashGenerator {

    /**
     * Hashes a plaintext password using BCrypt.
     * @param password The raw password (e.g., "secret_pass")
     * @return The secure, salted hash string.
     */
    public static String hashPassword(String password) {
        // BCrypt.gensalt() generates a unique, random salt for every user,
        // which is crucial for security.
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static void main(String[] args) {
        System.out.println("--- COPY THESE HASHES FOR DATABASE SEEDING ---");

        // Define simple test passwords for the four required initial users
        System.out.println("admin1 Hash: " + hashPassword("MyAdmin1Pass"));
        System.out.println("inst1 Hash:  " + hashPassword("MyInst1Pass"));
        System.out.println("stu1 Hash:   " + hashPassword("MyStu1Pass"));
        System.out.println("stu2 Hash:   " + hashPassword("MyStu2Pass"));

        System.out.println("----------------------------------------------");
    }
}