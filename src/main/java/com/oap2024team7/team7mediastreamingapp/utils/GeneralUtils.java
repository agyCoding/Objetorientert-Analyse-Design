package com.oap2024team7.team7mediastreamingapp.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

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
     * This method trims leading and trailing whitespace, replaces multiple spaces with a single space,
     * and capitalizes the first letter of each word while converting the rest of the characters to lowercase.
     * 
     * @param str the string to normalize
     * @return the normalized string
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
     * Method to normalize a number string (removed all non-digit characters from the string)
     * @param str
     * @return normalized number string
     */
    public static String normalizeNumString(String str) {
        return str.replaceAll("\\D+", "");
    }

    /**
     * Method to convert a string of special features to a set.
     * @param specialFeaturesString
     * @return set of special features
     */
    public Set<String> convertToSet(String specialFeaturesString) {
        Set<String> specialFeatures = new HashSet<>();
        if (specialFeaturesString != null && !specialFeaturesString.isEmpty()) {
            specialFeatures.addAll(Arrays.asList(specialFeaturesString.split(",")));
        }
        return specialFeatures;
    }

    /**
     * Method to check if a string is a number (integer or double)
     * @param str
     * @return true if the string is a number, false otherwise
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * This method takes a decimal string and formats it to a specified number of digits
     * in the integer and fractional parts. If the input string is not a valid number,
     * the method returns null.
     * 
     * @param str the decimal string to normalize
     * @param integerPart the number of digits in the integer part
     * @param fractionalPart the number of digits in the fractional part
     * @return normalized decimal string or null if the input is invalid
     */
    public static String normalizeDecimalFormat(String str, int integerPart, int fractionalPart) {
        if (str == null || str.isEmpty()) {
            return null;
        }
    
        // Step 1: Replace commas with periods to standardize the decimal separator
        // (Handling case where comma is used as a decimal separator)
        str = str.replace(",", ".");
    
        try {
            // Step 2: Parse the number using the US locale (which uses the period as a decimal separator)
            double number = Double.parseDouble(str);
    
            // Step 3: Round the number to the specified fractional part (2 decimals)
            double roundedNumber = Math.round(number * Math.pow(10, fractionalPart)) / Math.pow(10, fractionalPart);
    
            // Step 4: Format the number to 2 decimal places (ensure formatting is based on Locale.US for consistency)
            String formattedNumber = String.format(Locale.US, "%.2f", roundedNumber);
    
            // Step 5: Split the formatted number into integer and fractional parts
            String[] parts = formattedNumber.split("\\.");
            String integerPartStr = parts[0]; // Integer part of the number
    
            // Step 6: Validate integer part length
            if (integerPartStr.length() > integerPart) {
                return null; // Too many digits in integer part
            }
    
            // Step 7: Return the correctly formatted and rounded number
            return formattedNumber;
    
        } catch (NumberFormatException e) {
            return null; // In case of invalid format
        }
    }       
    
}
