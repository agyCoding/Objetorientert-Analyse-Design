package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Category;

import javafx.scene.control.ListCell;

/**
 * Custom ListCell for displaying Category objects with name only
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
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