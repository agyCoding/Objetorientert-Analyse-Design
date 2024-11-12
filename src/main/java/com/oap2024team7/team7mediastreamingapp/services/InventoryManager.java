package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Inventory;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Staff;

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
     * @param staff
     * @return A list of Inventory objects
     */
    public List<Inventory> checkInventoryForFilmAndStore(Film film, Staff staff) {
        String sql = "SELECT * FROM inventory WHERE film_id = ? AND store_id = ?";
        List<Inventory> inventories = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, film.getFilmId());
            stmt.setInt(2, staff.getStoreId());

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

    /**
     * Checks if a customer has an active rental for a given film and date range.
     * @param customerId The ID of the customer to check rentals for
     * @param filmId The ID of the film to check rentals for
     * @param rentalStartDate The start date of the rental period
     * @param rentalEndDate The end date of the rental period
     * @return true if the customer has an active rental, false otherwise
     */
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

    /**
     * Adds a rental to the database.
     * @param inventoryId The ID of the inventory to add a rental for
     * @param customer The customer to add the rental for
     * @param startDate The start date of the rental period
     * @param endDate The end date of the rental period
     * @return The ID of the rental that was added, or -1 if the rental could not be added
     */
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

    /**
     * Removes a rental from the database.
     * @param rentalId The ID of the rental to remove
     * @return true if the rental was removed successfully, false otherwise
     */
    public boolean removeRentalFromDatabase(int rentalId) {
        String deleteQuery = "DELETE FROM rental WHERE rental_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, rentalId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes all rentals for a given inventory from the database.
     * @param inventoryId The ID of the inventory to remove rentals for
     * @return true if the rentals were removed successfully, false otherwise
     */
    public boolean removeRentalForInventory(int inventoryId) {
        String checkForRentals = "SELECT * FROM rental WHERE inventory_id = ?";
        String deleteQuery = "DELETE FROM rental WHERE inventory_id = ?";

        // Check if there are any rentals at all, if not, nothing needs to be deleted, so return true
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement stmt = connection.prepareStatement(checkForRentals)) {
                stmt.setInt(1, inventoryId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        // If there are some rentals, they need to be deleted
        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, inventoryId);
            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a payment to the database.
     * @param customer The customer to add the payment for
     * @param rentalId The ID of the rental to add the payment for
     * @param amount The amount of the payment
     * @param paymentDate The date of the payment
     * @return The ID of the payment that was added, or -1 if the payment could not be added
     */
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