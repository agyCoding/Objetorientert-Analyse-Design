package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import javafx.scene.control.ListCell;

public class RatingCell extends ListCell<Film.Rating> {
    @Override
    protected void updateItem(Film.Rating rating, boolean empty) {
        super.updateItem(rating, empty);
        setText(empty || rating == null ? "" : rating.name());
    }
}
