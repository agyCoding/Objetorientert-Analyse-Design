package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for the User Manager.
 * This class is responsible for managing User objects.
 * V1 only checks with customer table where we assume that the email is the username
 * V2 will check with the staff table where we have username and password
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class UserManager {

    /**
     * Checks if the user can log in.
     * @param userName
     * @param password
     * @return True if the user can log in, false otherwise
     */
    public boolean canLogin(String userName, String password) {
        // Check if the password is empty or null
        boolean passwordIsNull = password == null || password.isEmpty();
        String hashedPassword = null;
        
        if (!passwordIsNull) {
            // Hash the password only if it's not empty
            hashedPassword = PasswordUtils.hashPassword(password);
        }
    
        // First check if the user is active. Only active users can log in
        String query = "SELECT customer_id FROM customer WHERE email = ? AND active = 1";  // Updated query
    
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
    
                // Now, retrieve the main profile's hashed password for this customer
                String profileQuery = "SELECT hashed_password FROM profile WHERE customer_id = ? AND main_profile = 1";
                try (PreparedStatement profileStmt = conn.prepareStatement(profileQuery)) {
                    profileStmt.setInt(1, customerId);
                    ResultSet profileRs = profileStmt.executeQuery();
                    
                    if (profileRs.next()) {
                        String storedHashedPassword = profileRs.getString("hashed_password");

                        // Check if both the password and stored password are null
                        if (passwordIsNull && storedHashedPassword == null) {
                            System.out.println("Login successful (passwords are null)");
                            return true;  // Login successful when both passwords are null
                        }

                        // Check if the provided password matches the stored hashed password
                        if (hashedPassword != null && hashedPassword.equals(storedHashedPassword)) {
                            System.out.println("Login successful");
                            return true;  // Password is valid, login successful
                        } else {
                            System.out.println("Invalid password");
                        }
                    } else {
                        System.out.println("Main profile not found");
                    }
                }
            } else {
                System.out.println("Customer not found or inactive");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Login failed");
        return false;  // Login failed
    }    
}
