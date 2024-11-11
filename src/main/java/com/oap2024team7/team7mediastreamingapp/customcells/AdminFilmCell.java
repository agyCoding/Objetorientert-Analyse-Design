package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.controllers.admin.AdminPageController;
import com.oap2024team7.team7mediastreamingapp.models.Film;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.ListCell;


public class AdminFilmCell extends ListCell<Film> {
    private HBox hbox = new HBox();
    private CheckBox checkBox = new CheckBox();
    private Text filmItem = new Text();
    private Film film;
    private AdminPageController controller;
    
    public AdminFilmCell(AdminPageController controller) {
        this.controller = controller;
        hbox.getChildren().addAll(checkBox, filmItem);

        // Add listener to checkbox
        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                // Notify AdminPageController that this film is selected
                controller.notifyFilmSelected(film);
            } else {
                controller.notifyFilmDeselected(film);
            }
        });
    }

    @Override
    protected void updateItem(Film film, boolean empty) {
        super.updateItem(film, empty);
        if (empty || film == null) {
            setText(null);
            setGraphic(null);
            this.film = null;
            checkBox.setSelected(false);
        } else {
            this.film = film;
            filmItem.setText(film.getTitle() + " (" + film.getReleaseYear() + ")");
            setGraphic(hbox);
        }
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
