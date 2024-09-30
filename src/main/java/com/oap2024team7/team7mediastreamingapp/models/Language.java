package com.oap2024team7.team7mediastreamingapp.models;

/**
 * Class for the Language object.
 * This class is responsible for creating and managing Language objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class Language {
    private int languageId;
    private String languageName;

    public Language(int languageId, String languageName) {
        this.languageId = languageId;
        this.languageName = languageName;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}
