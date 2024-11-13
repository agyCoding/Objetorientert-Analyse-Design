package com.oap2024team7.team7mediastreamingapp.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Staff;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.services.CustomerManager;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.customcells.CustomerLVCell;
import javafx.stage.FileChooser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller class for the Admin User Management screen.
 * This class is responsible for handling user interactions with the Admins User Management screen.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class AdminUserManagementController {

    // User menu
    @FXML
    private Label loggedInUserLabel;
    @FXML
    private MenuButton userAccountMenuButton;
    @FXML
    private MenuItem editAccountMenuItem;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private VBox filterMenu;

    // Main content
    @FXML
    private ListView<Customer> userListView;
    @FXML
    private Label currentPageLabel;
    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;

    // Filters menu
    @FXML
    private ComboBox<String> userStatusComboBox;
    @FXML
    private ComboBox<String> subTypeComboBox;
    @FXML
    private TextField userNameTF;

    // Local variables
    private int offset = 0;
    private final int limit = 20; // Load 20 users per page
    private Staff loggedInStaff;
    private CustomerManager customerManager;
    private String selectedStatus;
    private String selectedSubType;
    private int isActive;
    private String userName;

    @FXML
    public void initialize() {
        loggedInStaff = SessionData.getInstance().getLoggedInStaff();

        // Handle edit account action
        editAccountMenuItem.setOnAction(event -> handleEditAccount());

        // Handle logout action
        logoutMenuItem.setOnAction(event -> handleLogout());

        // Set up filters
        userStatusComboBox.getItems().addAll("Active", "Inactive");
        userStatusComboBox.getSelectionModel().select(0);
        subTypeComboBox.getItems().addAll("All", "Free", "Premium");
        subTypeComboBox.getSelectionModel().select(0);

        customerManager = new CustomerManager();

        // Load users
        loadUsers();

        // Indidate how to present users in the LV (show only some information from the Customer class)
        userListView.setCellFactory(param -> new CustomerLVCell());
        
        // Handle next and previous page
        nextButton.setOnAction(event -> nextPage());
        prevButton.setOnAction(event -> previousPage());
    }

    // Handles the action when the user clicks the "Edit Account" menu item.
    private void handleEditAccount() {
        // Load the edit account screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/accountmanagement/editaccount.fxml"));
            Parent root = loader.load();

            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();
            popupStage.setTitle("Media Streaming and Rental - Edit Account");

            // Set the scene for the pop-up stage
            popupStage.setScene(new Scene(root));

            // Make the pop-up window modal (blocks interaction with other windows until closed)
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(loggedInUserLabel.getScene().getWindow());

            // Show the pop-up window
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();

            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the edit account screen", "An error occurred while trying to load the edit account screen");
        }
    }
    
    // Handles the action when the user clicks the "Logout" menu item.
    private void handleLogout() {
        // Logic to log out the user
        System.out.println("Logout clicked");
        SessionData.getInstance().clearSessionData();
        switchToLogin();
    }
    
    /**
     * Method to load users from the database and display them in the list view.
     */
    private void loadUsers() {
        // Logic to load users
        System.out.println("Loading users");
        List<Customer> users;
        int staffsStoreId = loggedInStaff.getStoreId();

        if(selectedStatus != null || selectedSubType != null || userName != null) {
            users = customerManager.filterCustomers(isActive, selectedSubType, userName, offset, limit, staffsStoreId);
        } else {
            users = customerManager.getAllCustomers(offset, limit, staffsStoreId);
        }
        
        userListView.getItems().clear();
        userListView.getItems().addAll(users);

        if (users.size() < limit || customerManager.getAllCustomers(offset + limit, limit, staffsStoreId).isEmpty()) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }

        // Disable the previous button if on the first page
        prevButton.setDisable(offset == 0);
    }

    /**
     * Method to apply filters to the user list.
     */
    @FXML
    private void applyFilters() {
        // Logic to apply filters
        System.out.println("Applying filters");
        selectedStatus = userStatusComboBox.getSelectionModel().getSelectedItem();
        if (selectedStatus.equals("Active")) {
            isActive = 1;
        } else {
            isActive = 0;
        }
        selectedSubType = subTypeComboBox.getSelectionModel().getSelectedItem().toUpperCase();
        userName = userNameTF.getText();

        // Reset offset when applying filters
        offset = 0;

        loadUsers();

        // Update the current page label
        updateCurrentPageLabel();

    }

    /**
     * Method to clear filters and display all users.
     */
    @FXML
    private void clearFilters() {
        // Logic to clear filters
        System.out.println("Clearing filters");

        userStatusComboBox.getSelectionModel().select(0);
        subTypeComboBox.getSelectionModel().select(0);
        userNameTF.clear();

        // Reset offset when clearing filters
        offset = 0;

        // Set filters to null
        applyFilters();

        // Load users
        loadUsers();

        // Update the current page label
        updateCurrentPageLabel();
    }

    // Pagination methods
    private void nextPage() {
        offset += limit;
        loadUsers();
        updateCurrentPageLabel();
    }

    private void previousPage() {
        if (offset > 0) {
            offset -= limit;
            loadUsers();
            updateCurrentPageLabel();
        }
    }

    // Update the current page label based on pagination
    private void updateCurrentPageLabel() {
        int currentPage = (offset / limit) + 1;
        currentPageLabel.setText("Page: " + currentPage);
    }

    /**
     * Method to save the users to a file.
     * The file is saved in the .txt format.
     */
    @FXML
    private void saveUsersToFile() {
        // Prompt user to select location to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save User Database");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        // Construct the file name using the current date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = LocalDateTime.now().format(formatter) + "_user_database.txt";
        fileChooser.setInitialFileName(fileName);

        File file = fileChooser.showSaveDialog(loggedInUserLabel.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Customer customer : userListView.getItems()) {
                    writer.write(customer.toString());
                    writer.newLine();
                }
                GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Users Exported", "Users have been successfully exported to the file.");
            } catch (IOException e) {
                e.printStackTrace();
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to save users", "An error occurred while trying to save the users to the file.");
            }
        }
    }

    /**
     * Method to change to the manage movies screen.
     */
    @FXML
    private void changeToManageMovies() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin/adminpage.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) loggedInUserLabel.getScene().getWindow();
            stage.setTitle("Streamify - Manage Movies");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the admin screen", "An error occurred while trying to load the admin screen.");
        }
    }

    /**
     * Method to change to the manage users screen.
     * Reloads the current screen.
     */
    @FXML
    private void changeToManageUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin/adminusermanagment.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) loggedInUserLabel.getScene().getWindow();
            stage.setTitle("Streamify - Manage Users");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the user management screen", "An error occurred while trying to load the user management screen.");
        }
    }

    // Redirect to the login screen
    @FXML
    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/contentmanagement/login.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) loggedInUserLabel.getScene().getWindow();
            stage.setTitle("Streamify - Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error alert if the login screen cannot be loaded
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the login screen", "En error occured while trying to load the registration screen");
        }
    }
}
