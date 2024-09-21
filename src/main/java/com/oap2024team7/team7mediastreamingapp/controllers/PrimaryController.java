package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class PrimaryController {

    @FXML
    private Label loggedInUserLabel;
    @FXML
    private MenuButton userProfileMenuButton;
    @FXML
    private MenuItem editProfileMenuItem;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private ListView<String> filmListView;
    @FXML
    private VBox filterMenu;
    @FXML
    private Button toggleFilterMenuButton; 

    
    private String currentUsername;
    
    public void initialize() {

        /* INITIALIZE USER MENU */

        // Example: setting the logged-in username
        loggedInUserLabel.setText("Logged in as: ");
    
        // Handle edit profile action
        editProfileMenuItem.setOnAction(event -> handleEditProfile());
    
        // Handle logout action
        logoutMenuItem.setOnAction(event -> handleLogout());

        /* INITIALIZE FILM LV */

        toggleFilterMenuButton.setOnAction(event -> toggleFilterMenu());

        // Example film data
        ObservableList<String> films = FXCollections.observableArrayList(
            "Film 1: Action, PG-13, 2021",
            "Film 2: Comedy, PG, 2020",
            "Film 3: Drama, PG, 2022"
        );

        // Set the film list in the ListView
        filmListView.setItems(films);

        // Optionally, handle single click to show more details
        filmListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedFilm = filmListView.getSelectionModel().getSelectedItem();
                if (selectedFilm != null) {
                    showFilmDetails(selectedFilm);
                }
            }
        });
    }

    public void getLoggedInUsername(String username) {
        currentUsername = username; // Example value
        loggedInUserLabel.setText("Logged in as: " + currentUsername);
    }
    
    private void handleEditProfile() {
        // Logic to edit the profile
        System.out.println("Edit Profile clicked");
    }
    
    private void handleLogout() {
        // Logic to log out the user
        System.out.println("Logout clicked");
        switchToLogin();
    }

    private void toggleFilterMenu() {
        if (filterMenu.isVisible()) {
            filterMenu.setVisible(false);
            toggleFilterMenuButton.setText(">>"); // Change button text to indicate action
        } else {
            filterMenu.setVisible(true);
            toggleFilterMenuButton.setText("<<"); // Change button text to indicate action
        }
    }

    private void showFilmDetails(String film) {
        // Logic to display more details about the selected film
        System.out.println("Film details: " + film);
    }

    @FXML
    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) loggedInUserLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the login screen", "En error occured while trying to load the registration screen");
        }
    }

}
