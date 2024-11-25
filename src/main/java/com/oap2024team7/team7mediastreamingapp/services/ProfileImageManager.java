package com.oap2024team7.team7mediastreamingapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Author: Saman Shaheen (@saman091) with help from Magnus Bjordammen @magnuuus
 */

public class ProfileImageManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sakila";
    private static final String DB_USER = "student";
    private static final String DB_PASSWORD = "student";

    /**
     * Stores a profile image in the database.
     * If a profile image already exists for the given profileId, it updates the image instead.
     * @param profileId The ID of the profile.
     * @param imageData The image data as a byte array.
     * @return True if the image was successfully stored or updated, false otherwise.
     */
    public boolean storeProfileImage(int profileId, byte[] imageData) {
        String insertOrUpdateSQL = 
            "INSERT INTO profile_image (profile_id, image) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE image = VALUES(image)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdateSQL)) {
            preparedStatement.setInt(1, profileId);
            preparedStatement.setBytes(2, imageData);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error storing or updating profile image: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a profile image from the database.
     * @param profileId The ID of the profile.
     * @return The image data as a byte array, or null if not found.
     */
    public byte[] retrieveProfileImage(int profileId) {
        String selectSQL = "SELECT image FROM profile_image WHERE profile_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setInt(1, profileId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBytes("image");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving profile image: " + e.getMessage());
        }
        return null;
    }

    /**
     * Deletes a profile image from the database.
     * @param profileId The ID of the profile.
     * @return True if the image was successfully deleted, false otherwise.
     */
    public boolean deleteProfileImage(int profileId) {
        String deleteSQL = "DELETE FROM profile_image WHERE profile_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, profileId);
            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting profile image: " + e.getMessage());
            return false;
        }
    }
}
