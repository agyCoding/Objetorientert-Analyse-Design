package com.oap2024team7.team7mediastreamingapp.controllers.admin;

import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.models.Staff;
import com.oap2024team7.team7mediastreamingapp.services.AddressManager;
import com.oap2024team7.team7mediastreamingapp.services.StaffManager;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

//*  @author Saman Shaheen (@saman091) */
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

    private Staff loggedInStaff;
    private int staffAddressId;
    private Address staffAddress;

    @FXML
    private void initialize() {
        emailField.setDisable(true);

        // Get the logged-in staff from the session data
        loggedInStaff = SessionData.getInstance().getLoggedInStaff();
        staffAddressId = loggedInStaff.getAddressId();

        if (loggedInStaff != null) {
            firstNameField.setText(loggedInStaff.getFirstName());
            lastNameField.setText(loggedInStaff.getLastName());
            emailField.setText(loggedInStaff.getEmail());

            staffAddress = AddressManager.getAddressById(staffAddressId);
            if (staffAddress != null) {
                addressField.setText(staffAddress.getAddress());
                districtField.setText(staffAddress.getDistrict());
                int cityId = staffAddress.getCityId();
                cityField.setText(AddressManager.getInstance().getCityNameFromCityId(cityId));
                postalCodeField.setText(staffAddress.getPostalCode());
                phoneField.setText(staffAddress.getPhone());
            } else {
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Address Not Found", "No address found for the logged-in staff.");
            }
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Staff Not Found", "No staff found with the provided email.");
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
            loggedInStaff.setFirstName(firstName);
            loggedInStaff.setLastName(lastName);

            staffAddress.setAddress(address);
            staffAddress.setDistrict(district);
            staffAddress.setCityId(AddressManager.getInstance().getCityIdFromCityName(city));
            staffAddress.setPostalCode(postalCode);
            staffAddress.setPhone(phone);

            StaffManager.updateStaff(loggedInStaff);
            AddressManager.updateAddress(staffAddress);

            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Account updated successfully", "Your account has been updated.");
        } catch (Exception e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Error updating account", "Could not update account information.");
        }
    }
}
