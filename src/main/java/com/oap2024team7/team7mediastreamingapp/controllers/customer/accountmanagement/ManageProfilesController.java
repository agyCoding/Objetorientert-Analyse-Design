package com.oap2024team7.team7mediastreamingapp.controllers.customer.accountmanagement;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.services.ProfileImageManager;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;
import com.oap2024team7.team7mediastreamingapp.utils.PasswordUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller class for the Manage Profiles screen.
 * This class is responsible for handling user input and displaying data on the Manage Profiles screen.
 * It allows the user to view, create, and switch between profiles.
 * @author Agata (Agy) Olaussen @agyCoding, Saman Shaheen @saman091 (editing profile picture)
 */
public class ManageProfilesController {

    @FXML
    private HBox profileContainer;

    private SessionData sessionData = SessionData.getInstance();
    private ProfileImageManager profileImageManager = new ProfileImageManager();

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

        // Retrieve profile image from the database
        byte[] imageBytes = profileImageManager.retrieveProfileImage(profile.getProfileId());
        Image profileImage;
        if (imageBytes != null) {
            profileImage = new Image(new ByteArrayInputStream(imageBytes));
        } else {
            profileImage = new Image(getClass().getResourceAsStream("/images/pfp.png"));
        }
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

        // Add "Change PFP" button
        Button changePFPButton = new Button("Change PFP");
        changePFPButton.setOnAction(event -> handleChangePFP(profile, imageView));
        box.getChildren().add(changePFPButton);

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

    private void handleChangePFP(Profile profile, ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) profileContainer.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Load the selected image
                Image newProfileImage = new Image(selectedFile.toURI().toString());
                imageView.setImage(newProfileImage);

                // Convert the image to byte array
                byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());

                // Save the image in the database
                boolean success = profileImageManager.storeProfileImage(profile.getProfileId(), imageBytes);

                if (!success) {
                    showAlert("Failed to save the profile picture.");
                }
            } catch (IOException e) {
                showAlert("Error processing the image: " + e.getMessage());
            }
        }
    }

    private void handleProfileClick(Profile profile) {
        if (profile.getHashedPassword() != null) {
            // Prompt for password if profile is protected
            Optional<String> passwordInput = promptForPassword();
            if (passwordInput.isPresent()) {
                String hashedPasswordInput = PasswordUtils.hashPassword(passwordInput.get());
                String storedHashedPassword = profile.getHashedPassword();
                if (hashedPasswordInput.equals(storedHashedPassword)) {
                    updateSessionProfile(profile);
                } else {
                    showAlert("Incorrect password!");
                }
            }
        } else {
            updateSessionProfile(profile);
        }
    }

    private void handleCreateProfileClick() {
        System.out.println("Create new profile clicked!");

        StageUtils.switchScene(
            (Stage) profileContainer.getScene().getWindow(),
            "createProfile",
            "Streamify - Create Profile");
    }

    private Optional<String> promptForPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Profile Password");
        dialog.setHeaderText("Enter the password for this profile:");

        return dialog.showAndWait();
    }

    private void updateSessionProfile(Profile profile) {
        sessionData.setCurrentProfile(profile);
        showAlert("Profile switched to: " + profile.getProfileName());

        StageUtils.switchScene(
            (Stage) profileContainer.getScene().getWindow(),
            "primary",
            "Streamify - Customer's Content Viewer");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}
