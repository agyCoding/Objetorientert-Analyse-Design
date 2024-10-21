package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Language;

import javafx.scene.control.ListCell;

public class LanguageCell extends ListCell<Language> {
    @Override
    protected void updateItem(Language language, boolean empty) {
        super.updateItem(language, empty);
        if (empty || language == null) {
            setText(null);
        } else {
            setText(language.getLanguageName());  // Display language name
        }
    }    
}
