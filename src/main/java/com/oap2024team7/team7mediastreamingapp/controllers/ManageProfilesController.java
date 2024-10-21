// Last Modified: 04.10.2024
package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.utils.PasswordUtils;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

import java.util.List;
import java.util.Optional;


public class ManageProfilesController {

    @FXML
    private HBox profileContainer;

    private SessionData sessionData = SessionData.getInstance();

    @FXML
    public void initialize() {
        
        // Fetch profiles from the database for the logged-in customer
        int customerId = sessionData.getLoggedInCustomer().getCustomerId();

        List<Profile> profiles = ProfileManager.getProfilesByCustomerId(customerId);

        // Dynamically create profile items and add them to the HBox
        for (Profile profile : profiles) {
            VBox profileBox = createProfileBox(profile);
            profileContainer.getChildren().add(profileBox);
        }

        // Add "Create Profile" button at the end
        VBox createProfileBox = createCreateProfileBox();
        profileContainer.getChildren().add(createProfileBox);
    }

    private VBox createProfileBox(Profile profile) {
        VBox box = new VBox();
        
        // Load the placeholder image
        Image profileImage = new Image(getClass().getResourceAsStream("/images/pfp.png"));
        ImageView imageView = new ImageView(profileImage);
        
        // Set size for the image view (optional)
        imageView.setFitWidth(100);  // Set the desired width
        imageView.setFitHeight(100);  // Set the desired height
        imageView.setPreserveRatio(true);  // Preserve aspect ratio
        
        Label profileName = new Label(profile.getProfileName());

        // Set an action for clicking the image
        imageView.setOnMouseClicked(event -> handleProfileClick(profile));

        // Add the image view and profile name to the VBox
        box.getChildren().addAll(imageView, profileName);
        
        return box;
    }

    private VBox createCreateProfileBox() {
        VBox box = new VBox();
        
        // Use the placeholder image for the "+" button
        ImageView plusImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/plus.png")));
        plusImageView.setFitWidth(100);  // Set a fixed width for the image
        plusImageView.setFitHeight(100);  // Set the desired height
        plusImageView.setPreserveRatio(true);  // Preserve the aspect ratio
    
        Label createProfileText = new Label("Create profile");
        
        plusImageView.setOnMouseClicked(event -> handleCreateProfileClick());
        box.getChildren().addAll(plusImageView, createProfileText);
        
        return box;
    }
    

    private void handleProfileClick(Profile profile) {
        if (profile.getHashedPassword() != null) {
            // Prompt for password if profile is protected
            Optional<String> passwordInput = promptForPassword();
            String hashedPasswordInput = PasswordUtils.hashPassword(passwordInput.get());
            String storedHashedPassword = profile.getHashedPassword();
            if (passwordInput.isPresent() && hashedPasswordInput.equals(storedHashedPassword)) {
                updateSessionProfile(profile);
            } else {
                showAlert("Incorrect password!");
            }
        } else {
            updateSessionProfile(profile);
        }
    }

    private void handleCreateProfileClick() {
        System.out.println("Create new profile clicked!");

        try {
            // Load the FXML file for the create profile scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/createprofile.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) profileContainer.getScene().getWindow();
            stage.setTitle("Streamify - Content Viewer");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the create profile screen", "An error occurred while trying to load the create profile screen");
        }
    }


    private Optional<String> promptForPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Profile Password");
        dialog.setHeaderText("Enter the password for this profile:");

        Optional<String> result = dialog.showAndWait();
        return result;  // Returns the Optional<String> directly
    }

    private void updateSessionProfile(Profile profile) {
        sessionData.setCurrentProfile(profile);
        showAlert("Profile switched to: " + profile.getProfileName());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/primary.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) profileContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load the primary view.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
    
}
