package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Customer.AccountType;


/**
 * Class for the Customer Manager.
 * This class is responsible for managing Customer objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class CustomerManager {
    /**
     * Registers a new customer in the database.
     * @param newCustomer
     * @return the customer_id of the newly registered customer
     */
    public static int registerNewCustomer(Customer newCustomer) {
        String insertQuery = "INSERT INTO customer (first_name, last_name, email, address_id, active, store_id, account_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, newCustomer.getFirstName());
            stmt.setString(2, newCustomer.getLastName());
            stmt.setString(3, newCustomer.getEmail());
            stmt.setInt(4, newCustomer.getAddressId());
            stmt.setInt(5, newCustomer.getActive());
            stmt.setInt(6, newCustomer.getStoreId());
            stmt.setString(7, newCustomer.getAccountType().toString());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated customer_id
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 if registration fails
        }
    }

    /**
     * Create Customer object from the databased, based on email=username
     * @param username
     * @return
     */
    public static Customer getCustomerByUsername(String username) {
        String selectQuery = "SELECT * FROM customer WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int customerId = rs.getInt("customer_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    int addressId = rs.getInt("address_id");
                    int active = rs.getInt("active");
                    LocalDate createDate = rs.getDate("create_date").toLocalDate();
                    AccountType accountType = AccountType.valueOf(rs.getString("account_type"));
                    int storeId = rs.getInt("store_id");

                    Customer customer = new Customer(customerId, firstName, lastName, username, addressId, active, createDate, accountType, storeId);

                    return customer;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update customer information in the database.
     * @param customer
     */
    public static void updateCustomer(Customer customer) {
        String updateQuery = "UPDATE customer SET first_name = ?, last_name = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setInt(3, customer.getCustomerId());
            stmt.executeUpdate();
            System.out.println("Customer updated in the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update customer subscription in the database.
     * @param customer
     */
    public static void updateSubscription(Customer customer) {
        String updateQuery = "UPDATE customer SET account_type = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, customer.getAccountType().toString());
            stmt.setInt(2, customer.getCustomerId());
            stmt.executeUpdate();
            System.out.println("Customer subscription updated in the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
