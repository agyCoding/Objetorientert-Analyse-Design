package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.services.UserManager;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    UserManager userManager = new UserManager();

    @FXML
    private void tryToLogin() {

        // Get info from username and password fields
        // V1 Customers in sakila don't have passwords, so we will use the email as the username while the password has to be empty
        // V2 The staff will login with username and password, and will be checked towards their table
        String usernameText = usernameField.getText();
        String passwordText = passwordField.getText();

        if (usernameText.isEmpty()) {
            GeneralUtils.showAlert(AlertType.ERROR, "Login Failed", "Username cannot be empty", "Please enter correct username");
            return;
        }

        // Check if the user can login
        // Hardcoded admin login for presentation purposes (v1)
        if (userManager.canLogin(usernameText,passwordText) || (usernameText.equals("admin") && passwordText.equals("admin"))) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/primary.fxml"));
                Parent root = loader.load();

                // Get the controller of the next scene
                PrimaryController primaryController = loader.getController();

                // TEMPORARY ADMIN HANDLING (just passing the admin as username)
                if (usernameText.equals("admin")) {
                    primaryController.getLoggedInUsername("admin");
                } else {
                    // Pass the username to the next scene's controller
                    primaryController.getLoggedInUsername(usernameText);
                }


                 // Get the current stage (window) and set the new scene
                 Stage stage = (Stage) usernameField.getScene().getWindow();
                 stage.setTitle("Media Streaming and Rental - Content Viewer");

                 stage.setScene(new Scene(root));
                 stage.show();              
            } catch (IOException e) {
                e.printStackTrace();

                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the main app/primary screen", "En error occured while trying to load the primary app");
            }
        }
        else {
            // If password and username not matching, show error
            GeneralUtils.showAlert(AlertType.ERROR, "Login Failed", "Invalig Username or Password", "Please enter correct username and password");

        }
    }

    @FXML
    private void switchToUserRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/registerCustomer.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Media Streaming and Rental - Register New Customer");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the registration screen", "En error occured while trying to load the registration screen");
        }
    }
}
