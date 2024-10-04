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
        String insertQuery = "INSERT INTO profile (customer_id, main_profile, profile_name, birth_date, hashed_password) VALUES (?, ?, ?, ?, ?)";
    
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
            stmt.setString(5, newProfile.getHashedPassword());       
    
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
    
    public static boolean updateProfile(Profile profile) {
        // Query to check if the profile name already exists for the same customer
        String checkUniqueNameQuery = "SELECT COUNT(*) FROM profile WHERE customer_id = ? AND profile_name = ? AND profile_id != ?";
        // Query to update the profile
        String updateQuery = "UPDATE profile SET profile_name = ?, birth_date = ?, hashed_password = ? WHERE profile_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection()) {
            
            // First, check if the profile name is unique for the customer
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUniqueNameQuery)) {
                checkStmt.setInt(1, profile.getCustomerId());
                checkStmt.setString(2, profile.getProfileName());
                checkStmt.setInt(3, profile.getProfileId());
    
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Profile name already exists for this customer
                        System.out.println("Profile name already exists for this customer.");
                        return false; // Return false, update should not proceed
                    }
                }
            }
    
            // Proceed with updating the profile if the name is unique
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, profile.getProfileName());
    
                // Handle null birthDate
                if (profile.getBirthDate() != null) {
                    stmt.setDate(2, Date.valueOf(profile.getBirthDate()));
                } else {
                    stmt.setNull(2, java.sql.Types.DATE); // Set null if birthDate is null
                }
    
                stmt.setString(3, profile.getHashedPassword());                

                stmt.setInt(4, profile.getProfileId());
    
                int affectedRows = stmt.executeUpdate();
    
                return affectedRows > 0;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if update fails
        }
    }    

    public static boolean deleteProfile(Profile profile) {
        String deleteQuery = "DELETE FROM profile WHERE profile_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            
            stmt.setInt(1, profile.getProfileId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if deletion fails
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
                    String hashedPassword = rs.getString("hashed_password");
                    return new Profile(profileId, customerId, isMainProfile, profileName, birthDate, hashedPassword);
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
                    LocalDate birthDate = rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null;
                    String hashedPassword = rs.getString("hashed_password");
                    Profile profile = new Profile(profileId, customerId, isMainProfile, profileName, birthDate, hashedPassword);
                    profiles.add(profile);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profiles;
    }

    /**
     * Check if a profile name is already taken for a given customer ID
     * @param customerId
     * @param profileName
     * @return True if the profile name is already taken, false otherwise
     */
    public static boolean isProfileNameTaken(int customerId, String profileName) {
        String checkQuery = "SELECT COUNT(*) FROM profile WHERE customer_id = ? AND profile_name = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            
            stmt.setInt(1, customerId);
            stmt.setString(2, profileName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if a profile with the same name exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false; // Return false if there is no existing profile with that name
    }
    
}
