package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.Date;

import com.oap2024team7.team7mediastreamingapp.models.Discount;

/**
 * Class for the Discount Manager.
 * This class is responsible for managing discounts in the database.
 * @author Judith Déné Schjønneberg
 */
public class DiscountManager {

    /**
     * Registers a discount in the database.
     * @param discount
     * @return the generated ID of the discount or -1 if the update failed
     */
    public int registerDiscount(Discount discount) {
        String sql = "INSERT INTO film_discount (film_id, discount_percentage, start_date, expiry_date) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, discount.getFilmId());
            pstmt.setDouble(2, discount.getDiscountPercentage());
            pstmt.setDate(3, java.sql.Date.valueOf(discount.getStartDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(discount.getEndDate()));
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        
        return generatedId;
    }

    /**
     * Updates a discount in the database.
     * @param discount
     * @return true if the discount was updated successfully, false otherwise
     */
    public boolean updateDiscount(Discount discount) {
        String sql = "UPDATE film_discount SET film_id = ?, discount_percentage = ?, start_date = ?, expiry_date = ? WHERE discount_id = ?";
        boolean updated = false;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, discount.getFilmId());
            pstmt.setDouble(2, discount.getDiscountPercentage());
            pstmt.setDate(3, java.sql.Date.valueOf(discount.getStartDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(discount.getEndDate()));
            pstmt.setInt(5, discount.getDiscountId());
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                updated = true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return updated;
    }

    /**
     * This method returns the current active discount for the given filmId.
     * An active discount is one where the expiry date is in the future, and the start date is earlier than the expiry date.
     * @param filmId
     * @return the active discount for the given filmId, or null if no active discount is found
     */
    public Discount getActiveDiscount(int filmId) {
        Discount activeDiscount = null;
        String sql = "SELECT * FROM film_discount WHERE film_id = ? AND expiry_date > ? ORDER BY start_date DESC LIMIT 1";
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, filmId);             // Set the film ID in the query
            pstmt.setDate(2, Date.valueOf(currentDate)); // Set current date for checking active discount
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Extract the discount details from the result set
                int discountId = rs.getInt("discount_id");
                double discountPercentage = rs.getDouble("discount_percentage");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate expiryDate = rs.getDate("expiry_date").toLocalDate();

                // Create the Discount object
                activeDiscount = new Discount(discountId, filmId, discountPercentage, startDate, expiryDate);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Handle SQL exceptions appropriately
            return null;
        }
        return activeDiscount;
    }
}
