package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
     */
    public static void registerNewCustomer(Customer newCustomer) {
        // Add new customer to the database
        String insertQuery = "INSERT INTO customer (first_name, last_name, email, address_id, active, store_id, account_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, newCustomer.getFirstName());
                stmt.setString(2, newCustomer.getLastName());
                stmt.setString(3, newCustomer.getEmail());
                stmt.setInt(4, newCustomer.getAddressId());
                stmt.setInt(5, newCustomer.getActive());
                stmt.setInt(6, newCustomer.getStoreId());
                stmt.setString(7, newCustomer.getAccountType().toString());
                stmt.executeUpdate();
                System.out.println("Customer is added to the database.");           
        } catch (Exception e) {
            e.printStackTrace();
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
}
