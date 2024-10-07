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

    public class LoginResult {
        private boolean success;
        private String userType; // "customer" or "staff"
    
        public LoginResult(boolean success, String userType) {
            this.success = success;
            this.userType = userType;
        }
    
        public boolean isSuccess() {
            return success;
        }
    
        public String getUserType() {
            return userType;
        }
    }
    
    public LoginResult canLogin(String userName, String password) {
        boolean passwordIsNull = password == null || password.isEmpty();
        String hashedPassword = null;
        
        if (!passwordIsNull) {
            hashedPassword = PasswordUtils.hashPassword(password);
        }
    
        String customerQuery = "SELECT customer_id FROM customer WHERE email = ? AND active = 1";
        String staffQuery = "SELECT staff_id FROM staff WHERE username = ?";
    
        try (Connection conn = DatabaseManager.getConnection()) {
            // Check in customer table
            try (PreparedStatement stmt = conn.prepareStatement(customerQuery)) {
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int customerId = rs.getInt("customer_id");
                    // Retrieve the main profile's hashed password for this customer
                    String profileQuery = "SELECT hashed_password FROM profile WHERE customer_id = ? AND main_profile = 1";
                    try (PreparedStatement profileStmt = conn.prepareStatement(profileQuery)) {
                        profileStmt.setInt(1, customerId);
                        ResultSet profileRs = profileStmt.executeQuery();
                        if (profileRs.next()) {
                            String storedHashedPassword = profileRs.getString("hashed_password");
                            if (passwordIsNull && storedHashedPassword == null) {
                                return new LoginResult(true, "customer");
                            }
                            if (hashedPassword != null && hashedPassword.equals(storedHashedPassword)) {
                                return new LoginResult(true, "customer");
                            }
                        }
                    }
                }
            }
    
            // Check in staff table
            try (PreparedStatement stmt = conn.prepareStatement(staffQuery)) {
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int staffId = rs.getInt("staff_id");
                    // Retrieve staff's hashed password
                    String staffPasswordQuery = "SELECT password FROM staff WHERE staff_id = ?";
                    try (PreparedStatement staffStmt = conn.prepareStatement(staffPasswordQuery)) {
                        staffStmt.setInt(1, staffId);
                        ResultSet staffRs = staffStmt.executeQuery();
                        if (staffRs.next()) {
                            String storedHashedPassword = staffRs.getString("password");
                            if (passwordIsNull && storedHashedPassword == null) {
                                return new LoginResult(true, "staff");
                            }
                            if (hashedPassword != null && hashedPassword.equals(storedHashedPassword)) {
                                return new LoginResult(true, "staff");
                            }
                        }
                    }
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new LoginResult(false, null);  // Login failed
    }
    
}
