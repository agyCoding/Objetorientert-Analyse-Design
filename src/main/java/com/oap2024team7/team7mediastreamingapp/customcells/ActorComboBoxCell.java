package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Actor;
import javafx.scene.control.ListCell;

/**
 * Custom ListCell for displaying Actor objects in a ComboBox.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class ActorComboBoxCell extends ListCell<Actor> {
    @Override
    protected void updateItem(Actor actor, boolean empty) {
        super.updateItem(actor, empty);
        if (empty || actor == null) {
            setText(null);
        } else {
            setText(actor.getFirstName() + " " + actor.getLastName());
        }
    }
}