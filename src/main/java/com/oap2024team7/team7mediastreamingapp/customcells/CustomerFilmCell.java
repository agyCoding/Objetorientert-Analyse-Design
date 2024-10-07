package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import javafx.scene.control.ListCell;

public class CustomerFilmCell extends ListCell<Film> {
    @Override
    protected void updateItem(Film film, boolean empty) {
        super.updateItem(film, empty);
        if (empty || film == null) {
            setText(null);
        } else {
            // Display the title and release year in the ListView
            setText(film.getTitle() + " (" + film.getReleaseYear() + ")");
        }
    }
}
