package com.oap2024team7.team7mediastreamingapp.controllers.admin;



import com.oap2024team7.team7mediastreamingapp.models.Admin;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.services.AddressManager;
import com.oap2024team7.team7mediastreamingapp.services.AdminManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class EditAdminAccountController {

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

    private Admin loggedInAdmin;
    private Address adminAddress;

    @FXML
    private void initialize() {
        emailField.setDisable(true);

        // Get the logged-in admin from the session data
        loggedInAdmin = SessionData.getInstance().getLoggedInAdmin();
        adminAddress = SessionData.getInstance().getAdminAddress();

        if (loggedInAdmin != null) {
            firstNameField.setText(loggedInAdmin.getFirstName());
            lastNameField.setText(loggedInAdmin.getLastName());
            emailField.setText(loggedInAdmin.getEmail());

            addressField.setText(adminAddress.getAddress());
            districtField.setText(adminAddress.getDistrict());
            int cityId = adminAddress.getCityId();
            cityField.setText(AddressManager.getInstance().getCityNameFromCityId(cityId));

            postalCodeField.setText(adminAddress.getPostalCode());
            phoneField.setText(adminAddress.getPhone());
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Admin Not Found", "No admin found with the provided email.");
        }
    }

    public void tryToEditAccount() {
        System.out.println("Edit account button clicked");

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String address = addressField.getText();
        String district = districtField.getText();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();
        String phone = phoneField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || city.isEmpty() || postalCode.isEmpty() || phone.isEmpty()) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Please fill in all fields", "All fields are required.");
            return;
        }

        try {
            loggedInAdmin.setFirstName(firstName);
            loggedInAdmin.setLastName(lastName);

            adminAddress.setAddress(address);
            adminAddress.setDistrict(district);
            adminAddress.setCityId(AddressManager.getInstance().getCityIdFromCityName(city));
            adminAddress.setPostalCode(postalCode);
            adminAddress.setPhone(phone);

            AdminManager.updateAdmin(loggedInAdmin);
            AddressManager.updateAddress(adminAddress);

            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Account updated successfully", "Your account has been updated.");
        } catch (Exception e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Error updating account", "Could not update account information.");
        }
    }
}