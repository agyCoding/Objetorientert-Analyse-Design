package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for the Database Manager.
 * This class is responsible for managing the database connection and schema.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sakila";
    private static final String DB_USERNAME = "student";            
    private static final String DB_PASSWORD = "student";

    /**
     * Establishes a connection to the database.
     * @return Connection object
     * @throws SQLException
     */
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
    
    /**
     * Updates the database schema by adding a new column 'account_type' to the 'customer' table.
     * The column is of type ENUM with values 'FREE' and 'PREMIUM'.
     */
    public static void updateDatabaseSchema() {
        String checkColumnQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = 'customer' AND column_name = 'account_type'";
        String alterCustomerTable = "ALTER TABLE customer ADD account_type ENUM('FREE', 'PREMIUM') DEFAULT 'FREE';";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if the account_type column exists in the customer table
            ResultSet rs = stmt.executeQuery(checkColumnQuery);

            if (!rs.next()) { // Column doesn't exist
            stmt.executeUpdate(alterCustomerTable);
                System.out.println("Column 'account_type' added to customer table.");
            } else {
                System.out.println("Column 'account_type' already exists in customer table.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
