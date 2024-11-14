package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.oap2024team7.team7mediastreamingapp.models.Admin;

/**
 * Class for the Admin Manager.
 * This class is responsible for managing Admin objects.
 */

public class AdminManager {
    
    /**
     * Registers a new admin in the database.
     * @param newAdmin
     * @return the admin_id of the newly registered admin
     */
    public static int registerNewAdmin(Admin newAdmin) {
        String insertQuery = "INSERT INTO staff (first_name, last_name, address_id, email, store_id, active, username, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, newAdmin.getFirstName());
            stmt.setString(2, newAdmin.getLastName());
            stmt.setInt(3, newAdmin.getAddressId());
            stmt.setString(4, newAdmin.getEmail());
            stmt.setInt(5, newAdmin.getStoreId());
            stmt.setInt(6, newAdmin.getActive());
            stmt.setString(7, newAdmin.getUsername());
            stmt.setString(8, newAdmin.getPassword());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating admin failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated admin_id
                } else {
                    throw new SQLException("Creating admin failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 if registration fails
        }
    }

    /**
     * Retrieves an Admin object from the database based on username.
     * @param username
     * @return Admin object
     */
    public static Admin getAdminByUsername(String username) {
        String selectQuery = "SELECT * FROM staff WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int adminId = rs.getInt("staff_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    int addressId = rs.getInt("address_id");
                    String email = rs.getString("email");
                    int storeId = rs.getInt("store_id");
                    int active = rs.getInt("active");
                    String password = rs.getString("password");
                    LocalDate lastUpdate = rs.getDate("last_update").toLocalDate();

                    return new Admin(adminId, firstName, lastName, addressId, email, storeId, active, username, password, lastUpdate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update admin information in the database.
     * @param admin
     */
    public static void updateAdmin(Admin admin) {
        String updateQuery = "UPDATE staff SET first_name = ?, last_name = ?, address_id = ?, active = ?, username = ?, password = ? WHERE staff_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, admin.getFirstName());
            stmt.setString(2, admin.getLastName());
            stmt.setInt(3, admin.getAddressId());
            stmt.setInt(4, admin.getActive());
            stmt.setString(5, admin.getUsername());
            stmt.setString(6, admin.getPassword());
            stmt.setInt(7, admin.getAdminId());
            stmt.executeUpdate();
            System.out.println("Admin updated in the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all admins from the database.
     * @return List of all admins
     */
    public List<Admin> getAllAdmins() {
        String selectQuery = "SELECT * FROM staff ORDER BY last_name";
        List<Admin> admins = new ArrayList<>();
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
                LocalDate lastUpdate = rs.getDate("last_update").toLocalDate();

                Admin admin = new Admin(adminId, firstName, lastName, addressId, email, storeId, active, username, password, lastUpdate);
                admins.add(admin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return admins;
    }
}
