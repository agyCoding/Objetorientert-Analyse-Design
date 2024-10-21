package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.controllers.AdminFilmManagementController;
import javafx.scene.control.ListCell;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class AdminSpecialFeaturesCell extends ListCell<String> {
    private HBox hbox = new HBox();
    private CheckBox checkBox = new CheckBox();
    private Text featureItem = new Text();
    private AdminFilmManagementController controller;

    public AdminSpecialFeaturesCell(AdminFilmManagementController controller) {
        this.controller = controller;
        hbox.getChildren().addAll(checkBox, featureItem);

        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                controller.notifySpecialFeatureSelected(getItem());
            } else {
                controller.notifySpecialFeatureDeselected(getItem());
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
