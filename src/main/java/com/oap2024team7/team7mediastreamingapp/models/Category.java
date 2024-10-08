package com.oap2024team7.team7mediastreamingapp.models;

/**
 * Class for the Category (Genre) object.
 * This class is responsible for creating and managing Category objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class Category {
    private int categoryId;
    private String categoryName;

    // Basic constructor, from the DB
    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return categoryName; // Return the category name for display
    }
}
