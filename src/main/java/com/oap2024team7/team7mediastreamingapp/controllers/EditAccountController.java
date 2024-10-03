// Last Modified: 30.09.2024
package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.services.AddressManager;
import com.oap2024team7.team7mediastreamingapp.services.CustomerManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

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
    private Label accountTypeLabel;
    @FXML
    private Button upgradeToPremiumButton;

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

            // Address fields are saved on the address object, so we need to get the address object first
            addressField.setText(customersAddress.getAddress());
            districtField.setText(customersAddress.getDistrict());
            int cityId = customersAddress.getCityId();
            cityField.setText(AddressManager.getInstance().getCityNameFromCityId(cityId));

            postalCodeField.setText(customersAddress.getPostalCode());
            phoneField.setText(customersAddress.getPhone());
            accountTypeLabel.setText("Type: " + loggedInCustomer.getAccountType());

            // Set visibility of the Upgrade to Premium button based on account type
            setUpgradeButtonVisibility();
        } else {
            // Handle the case where the customer is not found in the current session
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Customer Not Found", "No customer found with the provided email.");
        }
    }

    public void setLoggedInCustomer(Customer customer) {
        loggedInCustomer = customer;
    }

    /**
     * Hide "Upgrade to Premium" button if the account already is premium.
     */
    private void setUpgradeButtonVisibility() {
        if (loggedInCustomer.getAccountType() == Customer.AccountType.PREMIUM) {
            upgradeToPremiumButton.setVisible(false);
        } else {
            upgradeToPremiumButton.setVisible(true);
        }
    }

    public void tryToEditAccount() {
        System.out.println("Edit account button clicked");
    
        // Retrieve values from the input fields
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
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

    public void tryToUpgradeToPremium() {
        System.out.println("Upgrade to Premium button clicked");
    
        // Check if the customer is already a premium customer
        if (loggedInCustomer.getAccountType() == Customer.AccountType.PREMIUM) {
            GeneralUtils.showAlert(AlertType.INFORMATION, "Already Premium", "Already a Premium Customer", "You are already a premium customer.");
            return;
        }
    
        // Call the upgrade method in the CustomerManager
        try {
            // Right now, you can upgrade when you just click
            // Implement some dummy payment method later
            loggedInCustomer.setAccountType(Customer.AccountType.PREMIUM);
            CustomerManager.updateSubscription(loggedInCustomer);
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Upgraded to Premium", "You have successfully upgraded to a premium account.");
            accountTypeLabel.setText("Type: " + loggedInCustomer.getAccountType());
            setUpgradeButtonVisibility();
        } catch (Exception e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Error upgrading to Premium", "Could not upgrade to premium account.");
        }
    }
}
