package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Language;

import javafx.scene.control.ListCell;

/**
 * Custom ListCell for displaying Language objects with language name only
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
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
