package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for the Database Manager.
 * This class is responsible for managing the database connection and schema.
 * @autor Agata (Agy) Olaussen (@agyCoding), Saman (for profile image path implementation)
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
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC driver not found", e);
        }

        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            return connection;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            throw e;
        }
    }

    /**
     * Updates the database schema by adding a new column 'account_type' to the 'customer' table,
     * creating a new 'profile' table, and adding a column for profile pictures.
     */
    public static void updateDatabaseSchema() {
        String alterCustomerTable = "ALTER TABLE customer ADD account_type ENUM('FREE', 'PREMIUM') DEFAULT 'FREE';";

        // When is_streamable is FALSE, only Premium customers should have access to stream the film, while Free accounts may only rent it.
        String alterFilmTable = "ALTER TABLE film ADD is_streamable BOOLEAN DEFAULT FALSE;";

        // Create the Profile table
        String createProfileTable = "CREATE TABLE IF NOT EXISTS profile (" +
            "profile_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "customer_id SMALLINT UNSIGNED NOT NULL, " +
            "main_profile BOOLEAN DEFAULT FALSE, " +
            "profile_name VARCHAR(255) NOT NULL, " +
            "profile_picture VARCHAR(255), " +
            "birth_date DATE, " +
            "hashed_password VARCHAR(255), " +
            "profile_image_path VARCHAR(255), " +
            "FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE" +
            ");";

        // Create My List table to store user's saved films
        String createMyListTable = "CREATE TABLE IF NOT EXISTS my_list (" +
            "list_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "profile_id INT NOT NULL, " +
            "film_id SMALLINT UNSIGNED NOT NULL, " +
            "FOREIGN KEY (profile_id) REFERENCES profile(profile_id) ON DELETE CASCADE, " +
            "FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE " +
            ");";

        // Create new table to store discount information
        String createDiscountTable = "CREATE TABLE IF NOT EXISTS film_discount (" +
            "discount_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "film_id SMALLINT UNSIGNED NOT NULL, " +
            "discount_percentage DECIMAL(4, 2) NOT NULL, " +
            "start_date DATE NOT NULL, " +
            "expiry_date DATE NOT NULL, " +
            "FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE " +
            ");";

        // Create new table for storing likes, dislikes and reviews for a film
        String createFilmReviewTable = "CREATE TABLE IF NOT EXISTS film_review (" +
            "review_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "film_id SMALLINT UNSIGNED NOT NULL, " +
            "profile_id INT NOT NULL, " +
            "liked BOOLEAN DEFAULT NULL, " +
            "review TEXT, " +
            "review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE, " +
            "FOREIGN KEY (profile_id) REFERENCES profile(profile_id) ON DELETE CASCADE " +
            ");";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            // Check if the account_type column exists in the customer table
            String checkColumnQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = 'customer' AND column_name = 'account_type'";
            ResultSet rs = stmt.executeQuery(checkColumnQuery);
            if (!rs.next()) { // Column doesn't exist
            stmt.executeUpdate(alterCustomerTable);
            System.out.println("Column 'account_type' added to customer table.");
            } else {
            System.out.println("Column 'account_type' already exists in customer table.");
            }

            // Check if the is_streamable column exists in the film table
            String checkFilmColumnQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = 'film' AND column_name = 'is_streamable'";
            rs = stmt.executeQuery(checkFilmColumnQuery);
            if (!rs.next()) { // Column doesn't exist
            stmt.executeUpdate(alterFilmTable);
            System.out.println("Column 'is_streamable' added to film table.");
            } else {
            System.out.println("Column 'is_streamable' already exists in film table.");
            }

            // Create the Profile table if it doesn't exist
            stmt.executeUpdate(createProfileTable);
            System.out.println("Table 'profile' created (if it didn't already exist).");

            // Create My List table if it doesn't exist
            stmt.executeUpdate(createMyListTable);
            System.out.println("Table 'my_list' created (if it didn't already exist).");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
