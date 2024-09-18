package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    // V1 only checks with customer table where we assume that the email is the username
    // V2 will check with the staff table where we have username and password
    public boolean canLogin(String userName, String password) {
        if (password == null || password.isEmpty()) {
            // Check also if the user is active. Only active users can log in
            String query = "SELECT COUNT(*) FROM customer WHERE email = ? AND active = 1";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Login successful");
                    return true;  // Login successful
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Login failed");
        return false;  // Login failed
    }
}
