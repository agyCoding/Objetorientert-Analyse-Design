// Last Modified: 30.09.2024
package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

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
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
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

    /**
     * Method to handle the login process.
     * It checks the username and password entered by the user.
     * If the login is successful, it checks if the user is a customer or staff member.
     * If the user is a customer, it checks if the customer has any profiles and if not, creates a default profile.
     * Then the method switches to the primary screen (customer) or admin page (staff).
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
                // Handle customer login
                handleCustomerLogin(usernameText);
            } else if ("staff".equals(userType)) {
                // Handle staff login
                handleStaffLogin(usernameText);
            }
        } else {
            // If password and username aren't correct, show an error alert
            GeneralUtils.showAlert(AlertType.ERROR, "Login Failed", "Invalid Username or Password", "Please enter correct username and password");
        }
    }

    /**
     * Method to handle the customer login process.
     * After successful login, it fetches the customer's address and saves the customer and address objects to the session data.
     * It then checks if the customer has any profiles and if not, creates a default profile.
     * Finally, it loads the films for the customer's profile (My List) and switches to the primary screen.
     */
    private void handleCustomerLogin(String usernameText) {
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
                newProfile = createDefaultProfile(dbCustomer);
            } else {
                // If profiles exist, save the main profile to session data
                newProfile = profiles.stream()
                        .filter(Profile::isMainProfile)
                        .findFirst()
                        .orElse(null);
            }

            // If a profile is found or created, proceed
            if (newProfile != null) {
                SessionData.getInstance().setCurrentProfile(newProfile);
                loadFilmsForProfile(newProfile);

                // Load primary screen
                StageUtils.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    "primary", // FXML short name
                    "Streamify - Customer's Content Viewer");
            }
        }
    }

    /**
     * Method to create a default profile for a customer.
     */
    private Profile createDefaultProfile(Customer dbCustomer) {
        Profile newProfile = new Profile(dbCustomer.getCustomerId(), dbCustomer.getFirstName(), null);
        newProfile.setIsMainProfile(true); // Set as the main profile
        int profileId = ProfileManager.registerNewProfile(newProfile);
        if (profileId != -1) {
            newProfile.setProfileId(profileId); // Set the generated ID in the profile object
            SessionData.getInstance().setCurrentProfile(newProfile); // Save the newly created profile in the session
            return newProfile;
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Profile Creation Failed", "Unable to create the default profile", "Please try again later.");
            return null;
        }
    }

    /**
     * Method to load the films for a customer's profile (My List).
     */
    private void loadFilmsForProfile(Profile profile) {
        List<Film> savedFilms = FilmManager.getFilmsFromMyList(profile.getProfileId());
        if (savedFilms != null && !savedFilms.isEmpty()) {
            System.out.println("Loaded " + savedFilms.size() + " films for profile ID: " + profile.getProfileId());
        } else {
            System.out.println("No films loaded from the database for profile ID: " + profile.getProfileId());
        }
        SessionData.getInstance().setSavedFilms(savedFilms);
    }

    /**
     * Method to handle the staff login process.
     * After successful login, it fetches the staff member and saves the staff object to the session data.
     * It then loads the admin screen and switches to it.
     */
    private void handleStaffLogin(String usernameText) {
        SessionData.getInstance().setLoggedInStaff(StaffManager.getStaffByUsername(usernameText));

        // Load admin screen using the StageUtils
        StageUtils.switchScene(
            (Stage) usernameField.getScene().getWindow(),
            "adminPage", // FXML short name
            "Streamify - Admin Dashboard");
    }

    /**
     * Method to switch to the user registration screen.
     */
    @FXML
    private void switchToUserRegistration() {
        StageUtils.switchScene(
            (Stage) usernameField.getScene().getWindow(), 
            "registerCustomer", 
            "Streamify - Register New Customer");
    }
}
