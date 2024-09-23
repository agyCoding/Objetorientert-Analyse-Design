package com.oap2024team7.team7mediastreamingapp.models;

// Category (Genre) class
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
}
