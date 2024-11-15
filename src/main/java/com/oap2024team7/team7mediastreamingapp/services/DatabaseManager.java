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
        String checkColumnQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = 'customer' AND column_name = 'account_type'";
        String alterCustomerTable = "ALTER TABLE customer ADD account_type ENUM('FREE', 'PREMIUM') DEFAULT 'FREE';";

        // Create the Profile table with an additional 'profile_image_path' column
        String createProfileTable = "CREATE TABLE IF NOT EXISTS profile (" +
            "profile_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "customer_id SMALLINT UNSIGNED NOT NULL, " +
            "main_profile BOOLEAN DEFAULT FALSE, " +
            "profile_name VARCHAR(255) NOT NULL, " +
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

    /**
     * Updates the profile image path for a given profile.
     * @param profileId The ID of the profile.
     * @param imagePath The path to the profile image.
     */
    public static void updateProfileImagePath(int profileId, String imagePath) {
        String updateImagePathQuery = "UPDATE profile SET profile_image_path = ? WHERE profile_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateImagePathQuery)) {

            pstmt.setString(1, imagePath);
            pstmt.setInt(2, profileId);
            pstmt.executeUpdate();

            System.out.println("Profile image path updated for profile ID: " + profileId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches the profile image path for a given profile.
     * @param profileId The ID of the profile.
     * @return The path to the profile image, or null if not set.
     */
    public static String getProfileImagePath(int profileId) {
        String getImagePathQuery = "SELECT profile_image_path FROM profile WHERE profile_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getImagePathQuery)) {

            pstmt.setInt(1, profileId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("profile_image_path");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
