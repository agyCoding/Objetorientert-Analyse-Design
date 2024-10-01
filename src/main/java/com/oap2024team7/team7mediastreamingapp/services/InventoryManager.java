package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Inventory;
import com.oap2024team7.team7mediastreamingapp.models.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.Timestamp;

/**
 * InventoryManager class
 * This class is responsible for managing the inventory of films in the database
 * It provides methods for checking for available inventory for a given film and date range
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class InventoryManager {

    /**
     * Checks for available inventory for a given film, store, and date range.
     * @param filmId The ID of the film to check inventory for
     * @param storeId The ID of the store to check inventory for
     * @param startDate The start date of the rental period
     * @return A list of available Inventory objects
     */
    public List<Inventory> checkForAvailableInventory(int filmId, int storeId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT i.inventory_id " +
                     "FROM inventory i " +
                     "LEFT JOIN rental r ON i.inventory_id = r.inventory_id " +
                     "WHERE i.film_id = ? AND i.store_id = ? " +
                     "AND (r.inventory_id IS NULL OR r.return_date < ? OR r.rental_date > ?)";
        
        List<Inventory> availableInventories = new ArrayList<>();
    
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
    
            stmt.setInt(1, filmId);
            stmt.setInt(2, storeId);
            stmt.setTimestamp(3, Timestamp.valueOf(startDate));
            stmt.setTimestamp(4, Timestamp.valueOf(endDate)); // Check for conflicts on both dates
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    availableInventories.add(inventory);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return availableInventories;
    }

    public boolean customerHasActiveRental(int customerId, int filmId, LocalDateTime rentalStartDate, LocalDateTime rentalEndDate) {
        String sql = "SELECT COUNT(*) FROM rental r " +
                     "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                     "WHERE r.customer_id = ? AND i.film_id = ? " +
                     "AND (r.rental_date <= ? AND r.return_date >= ?)";
        
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
    
            stmt.setInt(1, customerId);
            stmt.setInt(2, filmId);
            stmt.setTimestamp(3, Timestamp.valueOf(rentalEndDate));
            stmt.setTimestamp(4, Timestamp.valueOf(rentalStartDate));
    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if there is an active rental
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    } 

    public int addRentalToDatabase(int inventoryId, Customer customer, LocalDateTime startDate, LocalDateTime endDate) {
        int customerId = customer.getCustomerId();
        int staffId = customer.getStoreId(); // Hardcoded staff ID = store ID for now
        String insertQuery = "INSERT INTO rental (rental_date, inventory_id, customer_id, return_date, staff_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setInt(2, inventoryId);
            stmt.setInt(3, customerId);
            stmt.setTimestamp(4, Timestamp.valueOf(endDate));
            stmt.setInt(5, staffId); 
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating rental failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating rental failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void removeRentalFromDatabase(int rentalId) {
        String deleteQuery = "DELETE FROM rental WHERE rental_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, rentalId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addPaymentToDatabase(Customer customer, int rentalId, double amount, LocalDateTime paymentDate) {
        int customerId = customer.getCustomerId();
        int staffId = customer.getStoreId(); // Hardcoded staff ID = store ID for now
        String insertQuery = "INSERT INTO payment (customer_id, staff_id, rental_id, amount, payment_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, staffId);
            stmt.setInt(3, rentalId);
            stmt.setDouble(4, amount);
            stmt.setTimestamp(5, Timestamp.valueOf(paymentDate));
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


}    