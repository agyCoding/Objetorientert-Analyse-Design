package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.ListCell;


public class AdminFilmCell extends ListCell<Film> {
    private HBox hbox = new HBox();
    private CheckBox checkBox = new CheckBox();
    private Text filmItem = new Text();
    
    public AdminFilmCell() {
        hbox.getChildren().addAll(checkBox, filmItem);
    }

    @Override
    protected void updateItem(Film film, boolean empty) {
        super.updateItem(film, empty);
        if (empty || film == null) {
            setText(null);
            setGraphic(null);
        } else {
            filmItem.setText(film.getTitle() + " (" + film.getReleaseYear() + ")");
            setGraphic(hbox);
        }
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
