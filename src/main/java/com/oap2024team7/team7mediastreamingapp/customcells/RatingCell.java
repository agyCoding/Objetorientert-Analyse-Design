package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import javafx.scene.control.ListCell;

public class RatingCell extends ListCell<Film.Rating> {
    @Override
    protected void updateItem(Film.Rating rating, boolean empty) {
        super.updateItem(rating, empty);

         // If the item is 'NONE', display it as an empty string
         if (empty || rating == Film.Rating.NONE) {
            setText("");  // Display as an empty string
        } else {
            setText(rating.name());  // Display the rating name (e.g., "G", "PG")
        }
    }
}
