package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class RentalManager {
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
     * @param  rental The rental to add
     * @return The ID of the rental that was added, or -1 if the rental could not be added
     */
    public int addRentalToDatabase(Rental rental) {
        String insertQuery = "INSERT INTO rental (rental_date, inventory_id, customer_id, return_date, staff_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(rental.getRentalDate()));
            stmt.setInt(2, rental.getInventoryId());
            stmt.setInt(3, rental.getCustomerId());
            stmt.setTimestamp(4, Timestamp.valueOf(rental.getReturnDate()));
            stmt.setInt(5, rental.getStaffId()); 
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
}
