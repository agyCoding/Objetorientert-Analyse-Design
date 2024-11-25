package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.controllers.admin.AdminFilmManagementController;
import com.oap2024team7.team7mediastreamingapp.controllers.admin.AdminAddFilmController;
import javafx.scene.control.ListCell;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Custom ListCell for displaying Actor objects in a ListView, in the admin page (with check boxes).
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class AdminSpecialFeaturesCell extends ListCell<String> {
    private HBox hbox = new HBox();
    private CheckBox checkBox = new CheckBox();
    private Text featureItem = new Text();
    @SuppressWarnings("unused")
    private Object controller;

    public AdminSpecialFeaturesCell(Object controller) {
        this.controller = controller;
        hbox.getChildren().addAll(checkBox, featureItem);

        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                if (controller instanceof AdminFilmManagementController) {
                    ((AdminFilmManagementController) controller).notifySpecialFeatureSelected(getItem());
                } else if (controller instanceof AdminAddFilmController) {
                    ((AdminAddFilmController) controller).notifySpecialFeatureSelected(getItem());
                }
            } else {
                if (controller instanceof AdminFilmManagementController) {
                    ((AdminFilmManagementController) controller).notifySpecialFeatureDeselected(getItem());
                } else if (controller instanceof AdminAddFilmController) {
                    ((AdminAddFilmController) controller).notifySpecialFeatureDeselected(getItem());
                }
            }
        });
    }

    @Override
    protected void updateItem(String feature, boolean empty) {
        super.updateItem(feature, empty);
        if (empty || feature == null) {
            setText(null);
            setGraphic(null);
            checkBox.setSelected(false);
        } else {
            featureItem.setText(feature);
            setGraphic(hbox);
        }
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
