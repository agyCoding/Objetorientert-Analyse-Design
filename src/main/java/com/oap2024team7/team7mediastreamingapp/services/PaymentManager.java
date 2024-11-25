package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Manages payments in the database.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class PaymentManager {
        /**
     * Adds a payment to the database.
     * @param payment The payment to add
     * @return The ID of the payment that was added, or -1 if the payment could not be added
     */
    public int addPaymentToDatabase(Payment payment) {
        String insertQuery = "INSERT INTO payment (customer_id, staff_id, rental_id, amount, payment_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getCustomerId());
            stmt.setInt(2, payment.getStaffId());
            stmt.setInt(3, payment.getRentalId());
            stmt.setDouble(4, payment.getAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(payment.getPaymentDate()));
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
