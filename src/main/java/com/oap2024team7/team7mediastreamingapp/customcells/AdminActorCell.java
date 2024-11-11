package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.controllers.admin.AdminFilmManagementController;
import com.oap2024team7.team7mediastreamingapp.controllers.admin.AdminAddFilmController;
import com.oap2024team7.team7mediastreamingapp.models.Actor;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.ListCell;

public class AdminActorCell extends ListCell<Actor> {
    private HBox hbox = new HBox();
    private CheckBox checkBox = new CheckBox();
    private Text actorItem = new Text();
    private Actor actor;
    private Object controller;

    public AdminActorCell(Object controller) {
        this.controller = controller;
        hbox.getChildren().addAll(checkBox, actorItem);

        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                if (controller instanceof AdminFilmManagementController) {
                    ((AdminFilmManagementController) controller).notifyActorSelected(actor);
                } else if (controller instanceof AdminAddFilmController) {
                    ((AdminAddFilmController) controller).notifyActorSelected(actor);
                }
            } else {
                if (controller instanceof AdminFilmManagementController) {
                    ((AdminFilmManagementController) controller).notifyActorDeselected(actor);
                } else if (controller instanceof AdminAddFilmController) {
                    ((AdminAddFilmController) controller).notifyActorDeselected(actor);
                }
            }
        });
    }

    @Override
    protected void updateItem(Actor actor, boolean empty) {
        super.updateItem(actor, empty);
        if (empty || actor == null) {
            setText(null);
            setGraphic(null);
            this.actor = null;
            checkBox.setSelected(false);
        } else {
            this.actor = actor;
            actorItem.setText(actor.getFirstName() + " " + actor.getLastName());
            setGraphic(hbox);
        }
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
    
}
