package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.oap2024team7.team7mediastreamingapp.models.Staff;

public class StaffManager {
    public static Staff getStaffByUsername(String username) {
        String selectQuery = "SELECT * FROM staff WHERE username = ?";
                try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    int staffId = rs.getInt("staff_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    int addressId = rs.getInt("address_id");
                    String email = rs.getString("email");
                    int storeId = rs.getInt("store_id");
                    int active = rs.getInt("active");
                    String password = rs.getString("password");

                    Staff staff = new Staff(staffId, firstName, lastName, addressId, email, storeId, active, username, password);                    
                    return staff;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
}
