package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Category;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryManager {
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
}
