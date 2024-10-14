package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.oap2024team7.team7mediastreamingapp.models.Film;

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
        String checkColumnQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = 'customer' AND column_name = 'account_type'";
        String alterCustomerTable = "ALTER TABLE customer ADD account_type ENUM('FREE', 'PREMIUM') DEFAULT 'FREE';";

        // Create the Profile table
        String createProfileTable = "CREATE TABLE IF NOT EXISTS profile (" +
            "profile_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "customer_id SMALLINT UNSIGNED NOT NULL, " +
            "main_profile BOOLEAN DEFAULT FALSE, " +
            "profile_name VARCHAR(255) NOT NULL, " +
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
     * Adds a film to the My List table for a given profile.
     * @param profileId The ID of the profile.
     * @param filmId The ID of the film to add.
     */
    public static void addFilmToMyList(int profileId, int filmId) {
        String insertQuery = "INSERT IGNORE INTO my_list (profile_id, film_id) VALUES (?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            conn.setAutoCommit(false); // Start a transaction
    
            stmt.setInt(1, profileId);
            stmt.setInt(2, filmId);
            stmt.executeUpdate();
    
            conn.commit(); // Commit the transaction
    
            System.out.println("Film added to My List.");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                Connection conn = getConnection();
                if (conn != null) {
                    conn.rollback(); // Rollback if there's an issue
                    System.err.println("Transaction rolled back due to an error.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }
    

    /**
     * Removes a film from the My List table for a given profile.
     * @param profileId The ID of the profile.
     * @param filmId The ID of the film to remove.
     */
    public static void removeFilmFromMyList(int profileId, int filmId) {
        String deleteQuery = "DELETE FROM my_list WHERE profile_id = ? AND film_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            conn.setAutoCommit(false); // Start a transaction

            stmt.setInt(1, profileId);
            stmt.setInt(2, filmId);
            stmt.executeUpdate();

            conn.commit(); // Commit the transaction

            System.out.println("Film removed from My List.");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                Connection conn = getConnection();
                if (conn != null) {
                    conn.rollback(); // Rollback if there's an issue
                    System.err.println("Transaction rolled back due to an error.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    /**
     * Retrieves the list of films from the My List table for a given profile.
     * @param profileId The ID of the profile.
     * @return A list of films that are in the profile's My List.
     */
    public static List<Film> getFilmsFromMyList(int profileId) {
        List<Film> films = new ArrayList<>();
        String selectQuery = "SELECT f.film_id, f.title, f.release_year, f.rating, f.description " +
                "FROM my_list ml " +
                "LEFT JOIN film f ON ml.film_id = f.film_id " +
                "WHERE ml.profile_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Creating a Film object using the new constructor
                Film film = new Film(
                    rs.getInt("film_id"),                // int filmId
                    rs.getString("title"),               // String title
                    rs.getString("description"),         // String description (if available, otherwise handle null)
                    rs.getInt("release_year"),           // int releaseYear
                    Film.Rating.valueOf(rs.getString("rating")) // Film.Rating rating (make sure to handle invalid cases)
                );
                films.add(film);
            }
            System.out.println("Number of films retrieved for profile ID " + profileId + ": " + films.size());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid rating value encountered.");
            e.printStackTrace();
        }
        return films;
    }
}
