package com.oap2024team7.team7mediastreamingapp.controllers;

import java.time.LocalDate;

import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.PasswordUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

        // Save the new profile to the database
        if (ProfileManager.registerNewProfile(newProfile) != -1) {
            GeneralUtils.showAlert(AlertType.INFORMATION, "Profile Created", "Profile created successfully", "The new profile has been created.");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/primary.fxml"));
                Parent root = loader.load();

                // Get the current stage (window) and set the new scene
                Stage stage = (Stage) profileNameTF.getScene().getWindow();
                stage.setTitle("Streamify - Content Viewer");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the main app/primary screen", "An error occurred while trying to load the primary app");
            }
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Profile Creation Failed", "Profile creation failed", "An error occurred while creating the profile.");
        }
    }

}
