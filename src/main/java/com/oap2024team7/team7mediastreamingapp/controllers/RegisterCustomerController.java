/**
 * RegisterCustomerController.java
 * 
 * Author: Team 7 - OAP 2024
 * Contributions: Agata (Sole contributor)
 * 
 * Purpose: 
 * The RegisterCustomerController handles the user registration process for new customers in the media streaming 
 * application. It manages the registration interface and performs necessary validations on user input. Upon 
 * successful registration, it creates a new customer record and stores related address information.
 * 
 * The controller is designed to:
 * - Gather input data from the registration form fields.
 * - Validate that all required fields are filled out before proceeding.
 * - Normalize input data and manage city and address registration using the AddressManager.
 * - Create and register a new Customer instance with the provided details.
 * - Provide feedback to the user through alerts about the registration status.
 * - Allow navigation back to the login screen upon successful registration.
 * 
 * Methods:
 * 
 * - `tryToRegisterNewCustomer()`: Validates user input for required fields, retrieves data from the form, 
 *   and attempts to register the new customer along with their address. Displays alerts for any errors 
 *   encountered during the process, including validation and registration failures.
 * 
 * - `switchToLogin()`: Switches the current view to the login screen, allowing users to log in after 
 *   successful registration. Handles any potential errors in loading the login interface.
 */


package com.oap2024team7.team7mediastreamingapp.controllers;

import java.io.IOException;
import java.time.LocalDate;

import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.services.AddressManager;
import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.services.CustomerManager;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;

public class RegisterCustomerController {
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker birthDateDP;

    @FXML
    private TextField addressField;

    @FXML
    private TextField districtField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField phoneField;

    public void tryToRegisterNewCustomer() {
        // Basic validation to check if fields are empty
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || 
            emailField.getText().isEmpty() || birthDateDP.getValue() == null ||
            addressField.getText().isEmpty() || districtField.getText().isEmpty() || 
            cityField.getText().isEmpty() || postalCodeField.getText().isEmpty() || 
            phoneField.getText().isEmpty()) {
            
            GeneralUtils.showAlert(AlertType.ERROR, "Validation Error", "Required Fields Missing", "Please fill in all required fields.");
            return; // Stop the process if any field is empty
        }
        try {
            // Get information from FXML fields
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            LocalDate birthDate = birthDateDP.getValue();
            String address = addressField.getText();
            address = GeneralUtils.normalizeString(address);
            String district = districtField.getText();
            district = GeneralUtils.normalizeString(district);
            String city = cityField.getText();
            String postalCode = postalCodeField.getText();
            postalCode = GeneralUtils.normalizeNumString(postalCode);
            String phone = phoneField.getText();
            phone = GeneralUtils.normalizeNumString(phone);
    
            // Get the cityId based on the city name
            AddressManager addressManager = new AddressManager();
            int cityId = addressManager.getCityIdFromCityName(city);
            if (cityId == -1) {
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Invalid City", "Unable to register this city.");
                return;
            }
    
            // Set the address details to the address object
            Address newAddress = new Address(address, district, cityId, postalCode, phone);
            SessionData.getInstance().setCustomerAddress(newAddress);

            int addressId = AddressManager.registerAddress(newAddress);
            if (addressId == -1) {
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Invalid Address", "Unable to register this address.");
                return;
            }

            // Use customer constructor
            Customer newCustomer = new Customer(firstName, lastName, email, addressId, 1, birthDate);
    
            // Register the new customer
            CustomerManager.registerNewCustomer(newCustomer);
            SessionData.getInstance().setLoggedInCustomer(newCustomer);
    
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Registration Successful", "Your account has been registered successfully.");
            
            switchToLogin();
    
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging purposes
            GeneralUtils.showAlert(AlertType.ERROR, "Registration Error", "An error occurred", "Unable to register user account. Please try again.");
        }
    }

    @FXML
    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the login screen", "En error occured while trying to load the registration screen");
        }
    }
    


}
