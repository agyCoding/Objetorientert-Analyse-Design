package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Inventory;
import com.oap2024team7.team7mediastreamingapp.models.Film;

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
     * Checks for inventory for a given film and store.
     * @param film
     * @param storeId
     * @return A list of Inventory objects
     */
    public List<Inventory> checkInventoryForFilmAndStore(Film film, int storeId) {
        String sql = "SELECT * FROM inventory WHERE film_id = ? AND store_id = ?";
        List<Inventory> inventories = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, film.getFilmId());
            stmt.setInt(2, storeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setFilmId(rs.getInt("film_id"));
                    inventory.setStoreId(rs.getInt("store_id"));
                    inventories.add(inventory);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventories;
    }

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
                     "AND (r.rental_date <= ? AND r.return_date >= ?) " +  // Check for overlapping rentals
                     "WHERE i.film_id = ? AND i.store_id = ? " +
                     "AND (r.inventory_id IS NULL)";  // Include only inventories without active rentals
        
        List<Inventory> availableInventories = new ArrayList<>();
    
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
    
            stmt.setTimestamp(1, Timestamp.valueOf(endDate));  // End date for overlapping check
            stmt.setTimestamp(2, Timestamp.valueOf(startDate));  // Start date for overlapping check
            stmt.setInt(3, filmId);
            stmt.setInt(4, storeId);
    
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

    /**
     * Gets the next available date for an inventory item for a given film and store.
     * @param filmId
     * @param storeId
     * @return The next available date as a LocalDateTime object
     */
    public LocalDateTime getNextAvailableDate(int filmId, int storeId) {
        String sql = "SELECT MIN(return_date) " +
                     "FROM rental r " +
                     "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                     "WHERE i.film_id = ? AND i.store_id = ? " +
                     "AND r.return_date IS NOT NULL " +
                     "AND r.return_date > NOW()";
    
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
    
            stmt.setInt(1, filmId);
            stmt.setInt(2, storeId);
    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp nextAvailableDate = rs.getTimestamp(1);
                    return nextAvailableDate.toLocalDateTime();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no result is found or an exception occurs
    }

    /**
     * Deletes available inventory from the database.
     * @param inventoryId The ID of the inventory to delete
     * @return true if the inventory was deleted successfully, false otherwise
     */
    public boolean deleteAvailableInventory(int inventoryId) {
        String sql = "DELETE FROM inventory WHERE inventory_id = ?";
    
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
    
            stmt.setInt(1, inventoryId);
            int affectedRows = stmt.executeUpdate();
    
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds inventory for a film to the database.
     * @param filmId The ID of the film to add inventory for
     * @param storeId The ID of the store to add inventory for
     * @param quantity The quantity of inventory to add
     * @return true if the inventory was added successfully, false otherwise
     */
    public boolean addInventoryForFilm(int filmId, int storeId, int quantity) {
        String insertQuery = "INSERT INTO inventory (film_id, store_id) VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement stmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

        for (int i = 0; i < quantity; i++) {
            stmt.setInt(1, filmId);
            stmt.setInt(2, storeId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
            throw new SQLException("Creating inventory failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (!generatedKeys.next()) {
                throw new SQLException("Creating inventory failed, no ID obtained.");
            }
            }
        }
        } catch (SQLException e) {
        e.printStackTrace();
        return false;
        }
        return true;
    }
}    