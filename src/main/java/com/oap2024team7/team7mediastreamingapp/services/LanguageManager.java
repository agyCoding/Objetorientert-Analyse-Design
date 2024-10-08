package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Language;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LanguageManager {

    /**
     * Helper method to extract language data and create a Language object
     * @param conn
     * @param languageId
     * @return Language object
     * @throws SQLException
     */
    public Language getLanguageById(Connection conn, int languageId) throws SQLException {
        String query = "SELECT * FROM language WHERE language_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, languageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Language(rs.getInt("language_id"), rs.getString("name"));
            }
        }
        return null;
    }

/**
 * Helper method to extract a list of languages from the database
 * @return List of Language objects
 */

    public List<Language> getAllLanguages() {
        List<Language> languages = new ArrayList<>();
        String query = "SELECT language_id, name FROM language";  // Adjust the table/field names as needed
    
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("language_id");
                String name = resultSet.getString("name");
                languages.add(new Language(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return languages;        
    }
}