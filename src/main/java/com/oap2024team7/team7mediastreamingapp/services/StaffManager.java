package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.oap2024team7.team7mediastreamingapp.models.Staff;

/**
 * Class for the Staff Manager.
 * This class is responsible for managing Staff objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class StaffManager {
    /**
     * Gets a Staff object from the database, based on the username.
     * @param username
     * @return Staff object
     */
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

    /**
     * Update admin information in the database.
     * @param staff
     */
    public static void updateStaff(Staff staff) {
        String updateQuery = "UPDATE staff SET first_name = ?, last_name = ?, address_id = ?, active = ?, username = ?, password = ? WHERE staff_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, staff.getFirstName());
            stmt.setString(2, staff.getLastName());
            stmt.setInt(3, staff.getAddressId());
            stmt.setInt(4, staff.getActive());
            stmt.setString(5, staff.getUsername());
            stmt.setString(6, staff.getPassword());
            stmt.setInt(7, staff.getStaffId());
            stmt.executeUpdate();
            System.out.println("Staff updated in the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all admins from the database.
     * @return List of all admins
     */
    public List<Staff> getAllStaffMembers() {
        String selectQuery = "SELECT * FROM staff ORDER BY last_name";
        List<Staff> staffMembers = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int adminId = rs.getInt("staff_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                int addressId = rs.getInt("address_id");
                int storeId = rs.getInt("store_id");
                int active = rs.getInt("active");
                String username = rs.getString("username");
                String password = rs.getString("password");

                Staff staff = new Staff(adminId, firstName, lastName, addressId, email, storeId, active, username, password);
                staffMembers.add(staff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staffMembers;
    }

    
}
