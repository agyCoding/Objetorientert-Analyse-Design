package com.oap2024team7.team7mediastreamingapp.controllers.customer.accountmanagement;

import com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement.PrimaryController;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.PasswordUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controller class for the Edit Profile screen.
 * It manages the profile editing interface and allows users to update or delete their profile information.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class EditProfileController {
    @FXML
    private TextField profileNameTF;
    @FXML
    private DatePicker birthDateDP;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;

    private PrimaryController primaryController;
    private Profile profileToEdit;

    // Setter for PrimaryController
    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }   

    public void initialize() {
        // Get the profile to edit from the session data
        profileToEdit = SessionData.getInstance().getCurrentProfile();

        // Set the profile name and birth date fields to the current values
        profileNameTF.setText(profileToEdit.getProfileName());
        birthDateDP.setValue(profileToEdit.getBirthDate());
    }

    /**
     * Method to handle the profile update process.
     * It validates the input fields and updates the profile in the database.
     */
    @FXML
    public void tryToUpdateProfile() {
        // Get profile information from input fields
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

        // Make sure that the user is unable to delete password from their main profile and that main profile owner is at least 18 years old
        boolean isMainProfile = profileToEdit.isMainProfile();
        if (isMainProfile && password.isEmpty()) {
            GeneralUtils.showAlert(AlertType.WARNING, "Invalid Input", "Password Required", "The main profile must have a password.");
            return;
        }
        if (isMainProfile && !ProfileManager.isAgeValid(newBirthDate, 18)) {
            GeneralUtils.showAlert(AlertType.WARNING, "Invalid Input", "Age Restriction", "The main profile must be at least 18 years old.");
            return;
        }

        String hashedPassword = null;
        // Hash the password if it is not empty
        if (!password.isEmpty()) {
            hashedPassword = PasswordUtils.hashPassword(password);
        } else {
            hashedPassword = null;
        }

        // Update the profile with the new values
        profileToEdit.setProfileName(newProfileName);
        profileToEdit.setBirthDate(newBirthDate);
        profileToEdit.setHashedPassword(hashedPassword);

        // Update the profile in the database
        if (ProfileManager.updateProfile(profileToEdit)) {
            // If the update was successful, show a success message
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success!", "Profile Updated", "The profile has been updated successfully.");

            // Reload the user data in the primary controller, in case age restrictions have changed or profile name has been updated
            primaryController.reloadUserData();

            // Close the window
            profileNameTF.getScene().getWindow().hide();
        } else {
            // If the update failed, show an error message
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Profile Update Failed", "An error occurred while updating the profile.");
        }
    }

    /**
     * Method to handle the profile deletion process.
     * It validates the deletion and deletes the profile from the database.
     */
    @FXML
    public void tryToDeleteProfile() {
        // Make sure that the user is unable to delete their main profile
        boolean isMainProfile = profileToEdit.isMainProfile();
        if (isMainProfile) {
            GeneralUtils.showAlert(AlertType.WARNING, "Invalid Action", "Main Profile", "The main profile cannot be deleted.");
            return;
        }

        // Delete the profile from the database
        if (ProfileManager.deleteProfile(profileToEdit)) {
            // If the deletion was successful, show a success message
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success!", "Profile Deleted", "The profile has been deleted successfully.");

            // Update SessionData to clear profile information from session data
            SessionData.getInstance().clearProfileData();

            // Change the scene to manageprofiles.fxml
            StageUtils.switchScene(
                (Stage) profileNameTF.getScene().getWindow(), 
                "manageProfiles", 
                "Streamify - Manage Profiles");

            // Close the window
            profileNameTF.getScene().getWindow().hide();
        } else {
            // If the deletion failed, show an error message
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Profile Deletion Failed", "An error occurred while deleting the profile.");
        }
    }


}
