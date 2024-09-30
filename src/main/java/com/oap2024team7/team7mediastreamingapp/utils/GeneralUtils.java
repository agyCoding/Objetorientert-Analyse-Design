package com.oap2024team7.team7mediastreamingapp.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Class for general methods that are used throughout the application.
 * This class is responsible for creating and managing general utility methods.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class GeneralUtils {
    /**
     * Method to show an alert dialog.
     * @param type
     * @param title
     * @param header
     * @param content
     */
    public static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }    
 
    /**
     * Method to normalize a string.
     * @param str
     * @return normalized string
     */
    public static String normalizeString(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        str = str.trim().replaceAll("\\s+", " ");
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : str.toCharArray()) {
            if (Character.isSpaceChar(c)) {
            capitalizeNext = true;
            result.append(c);
            } else if (capitalizeNext) {
            result.append(Character.toUpperCase(c));
            capitalizeNext = false;
            } else {
            result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    /**
     * Method to normalize a number string.
     * @param str
     * @return normalized number string
     */
    public static String normalizeNumString(String str) {
        return str.replaceAll("\\D+", "");
    }
}
