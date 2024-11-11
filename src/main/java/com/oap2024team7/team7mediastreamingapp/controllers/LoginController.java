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
import com.oap2024team7.team7mediastreamingapp.services.StaffManager;

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
        UserManager.LoginResult loginResult = userManager.canLogin(usernameText, passwordText);
        if (loginResult.isSuccess()) {
            String userType = loginResult.getUserType();

            if ("customer".equals(userType)) {
                // Load customer info and redirect to primary.fxml
                try {
                    Customer dbCustomer = CustomerManager.getCustomerByUsername(usernameText);

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
                            } else {
                                GeneralUtils.showAlert(AlertType.ERROR, "Profile Creation Failed", "Unable to create the default profile", "Please try again later.");
                                return;
                            }
                        } else {
                            // If profiles exist, save the main profile to session data
                            for (Profile profile : profiles) {
                                if (profile.isMainProfile()) {
                                    SessionData.getInstance().setCurrentProfile(profile);
                                    break;
                                }
                            }
                        }

                        // Load primary screen
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/primary.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        stage.setTitle("Media Streaming and Rental - Content Viewer");
                        stage.setScene(new Scene(root));
                        stage.show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the main app/primary screen", "An error occurred while trying to load the primary app");
                }
            } else if ("staff".equals(userType)) {
                // Load admin screen for staff
                try {
                    SessionData.getInstance().setLoggedInStaff(StaffManager.getStaffByUsername(usernameText));

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin/adminpage.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setTitle("Admin Dashboard");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the admin dashboard", "An error occurred while trying to load the admin screen");
                }
            }
        } else {
            // If password and username aren't correct, show an error alert
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
