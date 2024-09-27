package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sakila";
    private static final String DB_USERNAME = "student";            
    private static final String DB_PASSWORD = "student";

    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("Loading MySQL JDBC Driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC driver not found", e);
        }
    
        try {
            System.out.println("Attempting to connect to database...");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Connection successful!");
            return connection;
        } catch (SQLException e) {
            System.err.println("Error: Unable to connect to the database");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            throw e;
        }
    }
    
    public static void updateDatabaseSchema() {
        String alterCustomerTable = "ALTER TABLE customer ADD birth_date DATE;";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // Execute SQL command to alter the customer table
            stmt.executeUpdate(alterCustomerTable);

            System.out.println("Database schema updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
