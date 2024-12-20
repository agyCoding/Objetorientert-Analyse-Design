package com.oap2024team7.team7mediastreamingapp.controllers.customer.accountmanagement;

import java.time.LocalDate;

import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.PasswordUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for the Create Profile screen.
 * It manages the profile creation interface and allows users to create new profiles.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class CreateProfileController {
    @FXML
    private TextField profileNameTF;
    @FXML
    private DatePicker birthDateDP;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    
    private Profile newProfile;
    private Customer customer;

    public void initialize() {
        // Get the customer from the session data
        customer = SessionData.getInstance().getLoggedInCustomer();
    }

    /**
     * Method to handle the profile creation process.
     * If the profile is created successfully, it redirects the user to the main application screen.
     */
    @FXML
    public void tryToCreateProfile() {
        // Get the profile information from input fields
        String newProfileName = profileNameTF.getText();
        LocalDate newBirthDate = birthDateDP.getValue();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();
        
        // Check if either of the required fields is not set
        if (newProfileName == null || newProfileName.trim().isEmpty() || newBirthDate == null) {
            GeneralUtils.showAlert(AlertType.WARNING, "Invalid Input", "Please fill out the fields", "Both profile name and birth date must be set.");
            return;
        }

        // Check if the password fields match
        if (!password.equals(repeatPassword)) {
            GeneralUtils.showAlert(AlertType.WARNING, "Invalid Input", "Passwords do not match", "The passwords entered do not match.");
            return;
        }

        int customerId = customer.getCustomerId();

        // Check for existing profiles with the same name, for the same customer
        // The profile name must be unique for each customer
        if (ProfileManager.isProfileNameTaken(customerId, newProfileName)) {
            GeneralUtils.showAlert(AlertType.WARNING, "Profile Name Taken", "The profile name already exists", "Please choose a different profile name.");
            return;
        }

        // Create a new profile object
        newProfile = new Profile(customerId, newProfileName, newBirthDate);
        
        // Only hash the password if it is provided
        if (password != null && !password.trim().isEmpty()) {
            String hashedPassword = PasswordUtils.hashPassword(password);
            newProfile.setHashedPassword(hashedPassword);
        } else {
            newProfile.setHashedPassword(null); // Set to null if password is empty
        }

        // Save the new profile to the database and SessionData
        if (ProfileManager.registerNewProfile(newProfile) != -1) {
            GeneralUtils.showAlert(AlertType.INFORMATION, "Profile Created", "Profile created successfully", "The new profile has been created.");
            SessionData.getInstance().setCurrentProfile(newProfile);

            // Change scene to the main application screen
            StageUtils.switchScene(
                (Stage) profileNameTF.getScene().getWindow(), 
                "primary", 
                "Streamify - Customer's Content Viewer");
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Profile Creation Failed", "Profile creation failed", "An error occurred while creating the profile.");
        }
    }

}
