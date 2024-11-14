package com.oap2024team7.team7mediastreamingapp.controllers.customer.accountmanagement;

import java.time.LocalDate;

import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.models.Address;

import com.oap2024team7.team7mediastreamingapp.services.AddressManager;
import com.oap2024team7.team7mediastreamingapp.services.CustomerManager;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;

import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.PasswordUtils;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;

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

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

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
            phoneField.getText().isEmpty() || passwordField.getText().isEmpty() || 
            repeatPasswordField.getText().isEmpty()) {
            
            GeneralUtils.showAlert(AlertType.ERROR, "Validation Error", "Required Fields Missing", "Please fill in all required fields.");
            return; // Stop the process if any field is empty
        }
        try {
            // Get information from FXML fields
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            LocalDate birthDate = birthDateDP.getValue();

            if (!ProfileManager.isAgeValid(birthDate, 18)) {
                GeneralUtils.showAlert(AlertType.ERROR, "Age Restriction", "You must be at least 18 years old to register.", "Please enter a valid birth date.");
                return; // Stop the registration process
            }

            String address = addressField.getText();
            address = GeneralUtils.normalizeString(address);
            String district = districtField.getText();
            district = GeneralUtils.normalizeString(district);
            String city = cityField.getText();
            String postalCode = postalCodeField.getText();
            postalCode = GeneralUtils.normalizeNumString(postalCode);
            String phone = phoneField.getText();
            phone = GeneralUtils.normalizeNumString(phone);

            // Check if the password and repeat password match
            String password = passwordField.getText();
            String repeatPassword = repeatPasswordField.getText();
            if (!password.equals(repeatPassword)) {
                GeneralUtils.showAlert(AlertType.ERROR, "Password Mismatch", "Passwords do not match", "Please make sure the passwords match.");
                return;
            }

            // Hash the password using SHA-256
            String hashedPassword = PasswordUtils.hashPassword(password);
    
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
            Customer newCustomer = new Customer(firstName, lastName, email, addressId, 1);
  
            // Register the new customer
            int newCustomerId = CustomerManager.registerNewCustomer(newCustomer);
            if (newCustomerId == -1) {
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Registration Failed", "Unable to register the customer. Try again.");
                return;
            }
            newCustomer.setCustomerId(newCustomerId);

            // When creating new customer, also create a default profile
            Profile newProfile = new Profile(newCustomerId, firstName, birthDate);
            newProfile.setHashedPassword(hashedPassword);

            SessionData.getInstance().setLoggedInCustomer(newCustomer);
            SessionData.getInstance().setCurrentProfile(newProfile);

            // Create a default profile and set it as the main profile
            newProfile.setIsMainProfile(true); // Set the new profile as the main profile
            int profileId = ProfileManager.registerNewProfile(newProfile); // Create profile in DB
            if (profileId != -1) {
                newProfile.setProfileId(profileId); // Update the profile object with the generated ID
            } else {
                GeneralUtils.showAlert(AlertType.ERROR, "Profile Creation Failed", "Unable to create the default profile", "Please try again later.");
                return;
            }
    
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
        StageUtils.switchScene(
            (Stage) firstNameField.getScene().getWindow(), 
            "login", 
            "Login");
    }
}
