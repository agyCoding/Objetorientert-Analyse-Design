package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import javafx.scene.control.ListCell;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class AdminSpecialFeaturesCell extends ListCell<String>{
    private HBox hbox = new HBox();
    private CheckBox checkBox = new CheckBox();
    private Text featureItem = new Text();

    public AdminSpecialFeaturesCell() {
        hbox.getChildren().addAll(checkBox, featureItem);
    }

    @Override
    protected void updateItem(String feature, boolean empty) {
        super.updateItem(feature, empty);
        if (empty || feature == null) {
            setText(null);
            setGraphic(null);
        } else {
            featureItem.setText(feature);
            setGraphic(hbox);
        }
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
