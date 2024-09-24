package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.services.AddressManager;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;

public class EditAccountController {
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

    private Customer loggedInCustomer;

    private Address customersAddress;
    
    @FXML
    private void initialize() {
        // we're using email as username for customers so this field should be disabled
        emailField.setDisable(true);

        // Get the logged in customer from the session data
        loggedInCustomer = SessionData.getInstance().getLoggedInCustomer();
        customersAddress = SessionData.getInstance().getCustomerAddress();

        // Trying to get the info about customer and address from current session
        if (loggedInCustomer != null) {
            firstNameField.setText(loggedInCustomer.getFirstName());
            lastNameField.setText(loggedInCustomer.getLastName());
            emailField.setText(loggedInCustomer.getEmail());
            birthDateDP.setValue(loggedInCustomer.getBirthDate());

            // Address fields are saved on the address object, so we need to get the address object first
            addressField.setText(customersAddress.getAddress());
            districtField.setText(customersAddress.getDistrict());
            int cityId = customersAddress.getCityId();
            cityField.setText(AddressManager.getInstance().getCityNameFromCityId(cityId));

            postalCodeField.setText(customersAddress.getPostalCode());
            phoneField.setText(customersAddress.getPhone());


        } else {
            // Handle the case where the customer is not found in the current session
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Customer Not Found", "No customer found with the provided email.");
        }
    }

    public void setLoggedInCustomer(Customer customer) {
        loggedInCustomer = customer;
    }

    public void tryToEditAccount() {
        System.out.println("Edit account button clicked");

    /* 
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
            String birthDate = birthDateDP.getValue().toString();
            String address = addressField.getText();
            String district = districtField.getText();
            String city = cityField.getText();
            String postalCode = postalCodeField.getText();
            String phone = phoneField.getText();
            
            // Create a new Customer object
            Customer customer = new Customer(firstName, lastName, email, birthDate, address, district, city, postalCode, phone);
            
            // Update the customer in the database
            CustomerManager.updateCustomer(customer);
            
            // Show a success message
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Account Updated", "Your account has been updated successfully.");
        } catch (Exception e) {
            // Show an error message if something goes wrong
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Account Update Failed", "An error occurred while updating your account. Please try again.");
        }

        */
    }
 


}
