// Last Modified: 30.09.2024
package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.services.UserManager;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.services.CustomerManager;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.services.AddressManager;
import com.oap2024team7.team7mediastreamingapp.models.Profile;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.sql.SQLException;

/**
 * Controller class for the Login screen.
 * This class is responsible for handling user login and authentication.
 * It manages the login interface and redirects users to the main application screen upon successful login.
 * The controller also allows navigation to the user registration interface for new customers.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class LoginController {
    
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserManager userManager = new UserManager();

    private Customer customer;

    /**
     *   Initializes the controller, setting the focus on the username field and, if a customer 
     *   is already logged in, pre-fills the username field with their email.
     */
    @FXML
    private void initialize() {
        // Set the username field to be focused when the screen is loaded
        usernameField.requestFocus();
        // If the customer has just created an account, the username field will be pre-filled with their email
        customer = SessionData.getInstance().getLoggedInCustomer();
        if (customer != null) {
            usernameField.setText(customer.getEmail());
        }
    }

    /*
     * Method to handle the login process.
     * It checks the username and password entered by the user.
     * If the login is successful, it retrieves the customer information from the database and saves it in the session data.
     * It also checks if the customer has any profiles and if not, creates a default profile.
     * Then the method switches to the primary screen.
     */
    @FXML
    private void tryToLogin() {
        // Clear previous session data
        SessionData.getInstance().clearSessionData();

        // Get info from username and password fields
        String usernameText = usernameField.getText();
        String passwordText = passwordField.getText();

        if (usernameText.isEmpty()) {
            GeneralUtils.showAlert(AlertType.ERROR, "Login Failed", "Username cannot be empty", "Please enter a correct username");
            return;
        }

        // Check if the user can login
        if (userManager.canLogin(usernameText, passwordText)) {
            try {
                // Get the customer object from the database
                Customer dbCustomer = CustomerManager.getCustomerByUsername(usernameText); // This can throw SQLException

                // Ensure customer is found
                if (dbCustomer != null) {   
                    // Fetch customer address based on addressId
                    Address customersAddress = AddressManager.getAddressById(dbCustomer.getAddressId());
                    if (customersAddress == null) {
                        GeneralUtils.showAlert(AlertType.ERROR, "Login Failed", "Address not found", "Unable to retrieve customer address.");
                        return;
                    }

                    // Save the customer and address object to the session data
                    SessionData.getInstance().setLoggedInCustomer(dbCustomer);
                    SessionData.getInstance().setCustomerAddress(customersAddress);

                    // Check if the customer has any profiles
                    List<Profile> profiles = ProfileManager.getProfilesByCustomerId(dbCustomer.getCustomerId());
                    Profile newProfile = null;

                    if (profiles.isEmpty()) {
                        // If no profiles exist, create a default profile
                        newProfile = new Profile(dbCustomer.getCustomerId(), dbCustomer.getFirstName(), null);
                        newProfile.setIsMainProfile(true); // Set as the main profile
                        int profileId = ProfileManager.registerNewProfile(newProfile);
                        if (profileId != -1) {
                            newProfile.setProfileId(profileId); // Set the generated ID in the profile object
                            SessionData.getInstance().setCurrentProfile(newProfile); // Save the newly created profile in the session
                            System.out.println("New profile created with ID: " + profileId);
                        } else {
                            GeneralUtils.showAlert(AlertType.ERROR, "Profile Creation Failed", "Unable to create the default profile", "Please try again later.");
                            return;
                        }
                    } else {
                        System.out.println("Profiles found for customer ID: " + dbCustomer.getCustomerId());
                        // If profiles exist, save the main profile to session data
                        for (Profile profile : profiles) {
                            System.out.println("Checking profile ID: " + profile.getProfileId() + ", isMainProfile: " + profile.isMainProfile());
                            if (profile.isMainProfile()) {
                                SessionData.getInstance().setCurrentProfile(profile);
                                System.out.println("Main profile set with ID: " + profile.getProfileId());
                                break; // Break if the main profile is found
                            }
                        }
                    }

                    // Load primary screen - this is where IOException may occur
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/primary.fxml"));
                    Parent root = loader.load();

                    // Get the controller of the next scene
                    PrimaryController primaryController = loader.getController();
                    primaryController.setLoggedInUsername(usernameText);

                    // Get the current stage (window) and set the new scene
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setTitle("Media Streaming and Rental - Content Viewer");
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    // Handle the case where the customer is not found
                    GeneralUtils.showAlert(AlertType.ERROR, "Login Failed", "Customer not found", "Unable to find customer information.");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the main app/primary screen", "An error occurred while trying to load the primary app");
            }
        } else {
            // If password and username not matching, show error
            GeneralUtils.showAlert(AlertType.ERROR, "Login Failed", "Invalid Username or Password", "Please enter correct username and password");
        }
    }

    /**
     * Method to switch to the user registration screen.
     */
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
            // If an error occurs while trying to load the registration screen, show an error alert
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the registration screen", "En error occured while trying to load the registration screen");
        }
    }
}
