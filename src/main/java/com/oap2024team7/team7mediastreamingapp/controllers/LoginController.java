/**
 * LoginController.java
 * 
 * Author: Team 7 - OAP 2024
 * Contributions: Agata (Sole contributor)
 * 
 * Purpose: 
 * The LoginController is responsible for managing the login interface and handling user authentication for 
 * both customers and staff. It verifies the login credentials entered by the user and redirects them to the 
 * main application interface (primary view) upon successful login. Additionally, it allows navigation to 
 * the user registration interface for new customers.
 * 
 * The controller is designed to:
 * - Manage user input for username and password.
 * - Authenticate the user using the UserManager and CustomerManager services.
 * - Initialize fields based on session data and pre-fill login fields when applicable.
 * - Handle navigation to the primary content viewer after login or user registration screen for new users.
 * 
 * Methods:
 * 
 * - `initialize()`: Initializes the controller, setting the focus on the username field and, if a customer 
 *   is already logged in, pre-fills the username field with their email.
 * 
 * - `tryToLogin()`: Handles the login process by checking the username and password entered by the user. 
 *   If the login is successful, the method switches to the primary screen. It also handles the special case 
 *   for admin login (v1), where the login is hardcoded for presentation purposes.
 * 
 * - `switchToUserRegistration()`: Switches to the user registration interface for new customers, allowing 
 *   them to create an account if they haven't already.
 * 
 * - `setLoggedInUsername(String username)`: Passes the logged-in username to the next scene (primary content viewer).
 */



package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.services.UserManager;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.services.CustomerManager;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.services.AddressManager;

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

    private UserManager userManager = new UserManager();

    private Customer customer;

    @FXML
    private void initialize() {
        // Set the username field to be focused when the screen is loaded
        usernameField.requestFocus();
        // try to get customer object from session data and if not null, set the username field to the customer's email
        customer = SessionData.getInstance().getLoggedInCustomer();
        if (customer != null) {
            usernameField.setText(customer.getEmail());
        }
    }

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
                Customer dbCustomer = CustomerManager.getCustomerByUsername(usernameText);
                
                // Ensure customer is found
                if (dbCustomer != null) {
                    // Merge the local customer's birthDate and accountType with the DB customer object
                    if (customer != null) {
                        dbCustomer.setBirthDate(customer.getBirthDate()); // Preserve birthDate from local profile
                        dbCustomer.setAccountType(customer.getAccountType()); // Preserve AccountType from local profile
                    }
    
                    // Fetch customer address based on addressId
                    int customersAddressId = dbCustomer.getAddressId();
                    Address customersAddress = AddressManager.getAddressById(customersAddressId);
    
                    // Save the customer and address object to the session data
                    SessionData.getInstance().setLoggedInCustomer(dbCustomer);
                    SessionData.getInstance().setCustomerAddress(customersAddress);
    
                    // Load primary screen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/primary.fxml"));
                    Parent root = loader.load();
    
                    // Get the controller of the next scene
                    PrimaryController primaryController = loader.getController();
    
                    // Pass the username to the next scene's controller
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
