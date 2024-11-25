package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for the Database Manager.
 * This class is responsible for managing the database connection and schema.
 * @author Agata (Agy) Olaussen (@agyCoding) and Saman Shaheen @saman091 (createProfileImageTable, addColumnIfNotExists amd createTableIfNotExists methods)
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
     * Updates the database schema by adding a new column 'account_type' to the 'customer' table
     * and creating a new 'profile' table.
     */
    public static void updateDatabaseSchema() {
        String alterCustomerTable = "ALTER TABLE customer ADD account_type ENUM('FREE', 'PREMIUM') DEFAULT 'FREE';";

        // Create the Profile table
        String createProfileTable = "CREATE TABLE IF NOT EXISTS profile (" +
            "profile_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "customer_id SMALLINT UNSIGNED NOT NULL, " +
            "main_profile BOOLEAN DEFAULT FALSE, " +
            "profile_name VARCHAR(255) NOT NULL, " +
            "profile_picture VARCHAR(255), " +
            "birth_date DATE, " +
            "hashed_password VARCHAR(255)," +
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
            "FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE" +
            ");";
        String createProfileImageTable = "CREATE TABLE IF NOT EXISTS profile_image (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "profile_id INT NOT NULL, " +
            "image LONGBLOB, " +
            "FOREIGN KEY (profile_id) REFERENCES profile(profile_id) ON DELETE CASCADE" +
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
            "FOREIGN KEY (profile_id) REFERENCES profile(profile_id) ON DELETE CASCADE" +
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

            // Check if the necessary columns exist in the film table
            String checkFilmColumnQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = 'film' AND column_name IN ('is_streamable', 'is_reviewable', 'is_ratable')";
            rs = stmt.executeQuery(checkFilmColumnQuery);

            Set<String> existingColumns = new HashSet<>();
            while (rs.next()) {
                existingColumns.add(rs.getString("column_name"));
            }

            // SQL statements to add needed columns to the film table
            String alterFilmTable = "ALTER TABLE film ADD is_streamable BOOLEAN DEFAULT FALSE;";
            String alterFilmTableReviewable = "ALTER TABLE film ADD is_reviewable BOOLEAN DEFAULT TRUE;";
            String alterFilmTableRatable = "ALTER TABLE film ADD is_ratable BOOLEAN DEFAULT TRUE;";

            // Check each column and add missing ones
            if (!existingColumns.contains("is_streamable")) {
                stmt.executeUpdate(alterFilmTable);
                System.out.println("Column 'is_streamable' added to the film table.");
            }
            if (!existingColumns.contains("is_reviewable")) {
                stmt.executeUpdate(alterFilmTableReviewable);
                System.out.println("Column 'is_reviewable' added to the film table.");
            }
            if (!existingColumns.contains("is_ratable")) {
                stmt.executeUpdate(alterFilmTableRatable);
                System.out.println("Column 'is_ratable' added to the film table.");
            }

            // If all required columns exist, print the confirmation
            if (existingColumns.contains("is_streamable") && existingColumns.contains("is_reviewable") && existingColumns.contains("is_ratable")) {
                System.out.println("Columns 'is_streamable', 'is_reviewable', and 'is_ratable' already exist in the film table.");
            }

            // Create the Profile table if it doesn't exist
            stmt.executeUpdate(createProfileTable);
            System.out.println("Table 'profile' created (if it didn't already exist).");

            // Create My List table if it doesn't exist
            stmt.executeUpdate(createMyListTable);
            System.out.println("Table 'my_list' created (if it didn't already exist).");

            // Create Discount table if it doesn't exist
            stmt.executeUpdate(createDiscountTable);
            System.out.println("Table 'film_discount' created (if it didn't already exist).");

            // Create Film Review table if it doesn't exist
            stmt.executeUpdate(createFilmReviewTable);
            System.out.println("Table 'film_review' created (if it didn't already exist).");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Adds a column to a table if it does not exist.
     * @param stmt Statement object
     * @param tableName Name of the table
     * @param columnName Name of the column
     * @param alterQuery SQL query to add the column
     * @throws SQLException
     */
    private static void addColumnIfNotExists(Statement stmt, String tableName, String columnName, String alterQuery) throws SQLException {
        String checkColumnQuery = String.format(
            "SELECT column_name FROM information_schema.columns WHERE table_name = '%s' AND column_name = '%s'",
            tableName, columnName);
        try (ResultSet rs = stmt.executeQuery(checkColumnQuery)) {
            if (!rs.next()) {
                stmt.executeUpdate(alterQuery);
                System.out.printf("Column '%s' added to table '%s'.%n", columnName, tableName);
            } else {
                System.out.printf("Column '%s' already exists in table '%s'.%n", columnName, tableName);
            }
        }
    }

    /**
     * Creates a table if it does not exist.
     * @param stmt Statement object
     * @param tableName Name of the table
     * @param createQuery SQL query to create the table
     * @throws SQLException
     */
    private static void createTableIfNotExists(Statement stmt, String tableName, String createQuery) throws SQLException {
        stmt.executeUpdate(createQuery);
        System.out.printf("Table '%s' created (if it didn't already exist).%n", tableName);
    }
}
