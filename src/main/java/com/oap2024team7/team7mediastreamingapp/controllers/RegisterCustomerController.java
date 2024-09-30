// Last Modified: 30.09.2024
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

/**
 * Controller class for the Register Customer screen.
 * This class is responsible for handling user input and registering a new customer account.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

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

    /**
     * Method to handle the registration process.
     * It checks the user input and registers a new customer account (and, if needed, city and address).
     * If the registration is successful, the method switches to the login screen.
     */
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
                // Show an error message to the user
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Invalid Address", "Unable to register this address.");
                return;
            }

            // Use customer constructor
            Customer newCustomer = new Customer(firstName, lastName, email, addressId, 1, birthDate);
    
            // Register the new customer
            CustomerManager.registerNewCustomer(newCustomer);
            SessionData.getInstance().setLoggedInCustomer(newCustomer);
    
            // Show a success message to the user
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Registration Successful", "Your account has been registered successfully.");
            
            switchToLogin();
    
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging purposes
            // Show an error message to the user
            GeneralUtils.showAlert(AlertType.ERROR, "Registration Error", "An error occurred", "Unable to register user account. Please try again.");
        }
    }

    // Method to switch to the login screen
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
