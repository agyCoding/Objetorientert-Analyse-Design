// Last Modified: 30.09.2024
package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.services.AddressManager;
import com.oap2024team7.team7mediastreamingapp.services.CustomerManager;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;

/**
 * Controller class for the Edit Account screen.
 * This class is responsible for handling user input and updating the customer's account information.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

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
    
        // Retrieve values from the input fields
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        LocalDate birthDate = birthDateDP.getValue();
        String address = addressField.getText();
        String district = districtField.getText();
        String city = cityField.getText(); // You might need a method to get the cityId based on the city name
        String postalCode = postalCodeField.getText();
        String phone = phoneField.getText();
    
        // Validate inputs (add any necessary validation here)
        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || city.isEmpty() || postalCode.isEmpty() || phone.isEmpty()) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Please fill in all fields", "All fields are required.");
            return;
        }
    
        // Update the properties of the loggedInCustomer object directly
        try {
            loggedInCustomer.setFirstName(firstName);
            loggedInCustomer.setLastName(lastName);
            loggedInCustomer.setBirthDate(birthDate); // Assuming you added this method in the Customer class
    
            // Update the address object
            customersAddress.setAddress(address);
            customersAddress.setDistrict(district);
            customersAddress.setCityId(AddressManager.getInstance().getCityIdFromCityName(city)); // You may need to implement this method
            customersAddress.setPostalCode(postalCode);
            customersAddress.setPhone(phone); // Update phone number from the input field
    
            // Call update methods in your managers
            CustomerManager.updateCustomer(loggedInCustomer); // Update the existing customer directly
            AddressManager.updateAddress(customersAddress); // Update the address directly
    
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Account updated successfully", "Your account has been updated.");
        } catch (Exception e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Error updating account", "Could not update account information.");
        }
    }    

}
