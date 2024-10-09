package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Actor;
import com.oap2024team7.team7mediastreamingapp.models.Category;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Language;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for the Category Manager.
 * This class is responsible for managing Category objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class CategoryManager {
    /**
     * Fetches all categories from the database.
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        // Get all categories from the database
        String getQuery = "SELECT * FROM category";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            ResultSet rs = stmt.executeQuery();
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category category = new Category(rs.getInt("category_id"), rs.getString("name"));
                categories.add(category);
            }
            return categories;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Category getCategoryByFilmId(int filmId) {
        // Get the category of a film from the database
        String getQuery = "SELECT c.* FROM category c " +
                   "JOIN film_category fc ON c.category_id = fc.category_id " +
                   "WHERE fc.film_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int category_id = rs.getInt("category_id");
                String name = rs.getString("name");
                return new Category(category_id, name);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the category of an existing film.
     * @param film The film to update the category for
     * @param category The new category to set for the film
     * @return true if the update was successful, false otherwise
     */
    public boolean updateCategoryForFilm(Film film, Category category) {
        String updateQuery = "UPDATE film_category SET category_id = ? WHERE film_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, category.getCategoryId());
            stmt.setInt(2, film.getFilmId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
