package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Category;

import javafx.scene.control.ListCell;

public class CategoryCell extends ListCell<Category> {
    @Override
    protected void updateItem(Category category, boolean empty) {
        super.updateItem(category, empty);
        if (empty || category == null) {
            setText("");
        } else {
            setText(category.getCategoryName());
        }
    }
}