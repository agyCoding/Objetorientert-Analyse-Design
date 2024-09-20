package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.oap2024team7.team7mediastreamingapp.models.Customer;

public class CustomerManager {
    public static void registerNewCustomer(Customer newCustomer) {
        // Add new customer to the database
        String insertQuery = "INSERT INTO customer (first_name, last_name, email, address_id, active, store_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, newCustomer.getFirstName());
                stmt.setString(2, newCustomer.getLastName());
                stmt.setString(3, newCustomer.getEmail());
                stmt.setInt(4, newCustomer.getAddressId());
                stmt.setInt(5, newCustomer.getActive());
                stmt.setInt(6, 1); // hardcoder store_id 1 since we're not using stores in the app, but the field is NN
                stmt.executeUpdate();
                System.out.println("Customer is added to the database.");           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
