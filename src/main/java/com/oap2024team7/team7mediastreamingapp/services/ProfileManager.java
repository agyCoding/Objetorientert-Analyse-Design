package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Profile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileManager {
    /**
     * Register a new profile in the database
     * @param newProfile
     * @return The profile ID of the newly registered profile
     */
    public static int registerNewProfile(Profile newProfile) {
        String insertQuery = "INSERT INTO profile (customer_id, main_profile, profile_name, birth_date) VALUES (?, ?, ?, ?)";
    
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, newProfile.getCustomerId());
            stmt.setBoolean(2, newProfile.isMainProfile());
            stmt.setString(3, newProfile.getProfileName());
    
            // Handle null birthDate
            if (newProfile.getBirthDate() != null) {
                stmt.setDate(4, Date.valueOf(newProfile.getBirthDate()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE); // Set null if birthDate is null
            }
    
            int affectedRows = stmt.executeUpdate();
    
            if (affectedRows == 0) {
                throw new SQLException("Creating profile failed, no rows affected.");
            }
    
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated profile_id
                } else {
                    throw new SQLException("Creating profile failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 if registration fails
        }
    }
    

    /**
     * Get all information about a certain profile based on the given profileId
     * @param profileId
     * @return Profile object containing all information
     */
    public static Profile getProfileById(int profileId) {
        String selectQuery = "SELECT * FROM profile WHERE profile_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            
            stmt.setInt(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int customerId = rs.getInt("customer_id");
                    boolean isMainProfile = rs.getBoolean("main_profile");
                    String profileName = rs.getString("profile_name");
                    LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                    return new Profile(profileId, customerId, isMainProfile, profileName, birthDate);
                } else {
                    return null; // Profile not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Return null if query fails
        }
    }


    /**
     * Get a list of profiles for a given customer ID
     * @param customerId
     * @return List of profiles
     */
    public static List<Profile> getProfilesByCustomerId(int customerId) {
        String selectQuery = "SELECT * FROM profile WHERE customer_id = ?";
        List<Profile> profiles = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int profileId = rs.getInt("profile_id");
                    boolean isMainProfile = rs.getBoolean("main_profile");
                    String profileName = rs.getString("profile_name");
                    LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                    Profile profile = new Profile(profileId, customerId, isMainProfile, profileName, birthDate);
                    profiles.add(profile);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profiles;
    }

}
