package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;

import java.io.IOException;
import java.util.List;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

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
    private VBox filterMenu;
    @FXML
    private Button toggleFilterMenuButton;
    @FXML
    private ListView<String> filmListView;
    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private RadioButton sortByTitle;
    @FXML
    private RadioButton sortByReleaseYear;
    
    private ToggleGroup sortToggleGroup;
    private String currentUsername;
    private FilmManager filmManager;
    private int offset = 0;
    private final int limit = 20; // Load 20 films per page
    
    @FXML
    public void initialize() {
        filmManager = new FilmManager();

        // Initialize the ToggleGroup in the controller
        sortToggleGroup = new ToggleGroup();
        
        // Assign the ToggleGroup to the RadioButtons
        sortByTitle.setToggleGroup(sortToggleGroup);
        sortByReleaseYear.setToggleGroup(sortToggleGroup);

        // Set default selection if needed
        sortByTitle.setSelected(true);

        /* INITIALIZE USER MENU */

        // Example: setting the logged-in username
        loggedInUserLabel.setText("Logged in as: ");
    
        // Handle edit profile action
        editProfileMenuItem.setOnAction(event -> handleEditProfile());
    
        // Handle logout action
        logoutMenuItem.setOnAction(event -> handleLogout());

        /* INITIALIZE FILM LV */

        toggleFilterMenuButton.setOnAction(event -> toggleFilterMenu());

        // Load the first page of films
        loadFilms();

        // Handle next and previous page
        nextButton.setOnAction(event -> nextPage());
        prevButton.setOnAction(event -> previousPage());

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

    private void loadFilms() {
        List<Film> films = filmManager.getAllFilms(offset, limit);
        filmListView.getItems().clear();
        for (Film film : films) {
            filmListView.getItems().add(film.getTitle() + " (" + film.getreleaseYear() + ")");
        }
    }

    private void nextPage() {
        offset += limit;
        loadFilms();
    }

    private void previousPage() {
        if (offset > 0) {
            offset -= limit;
            loadFilms();
        }
    }

    private void sortFilms() {
        String selectedSort = sortComboBox.getValue();
        List<Film> sortedFilms;
        if ("Sort by Title".equals(selectedSort)) {
            sortedFilms = filmManager.getFilmsSortedByTitle();
        } else {
            sortedFilms = filmManager.getFilmsSortedByYear();
        }
        filmListView.getItems().clear();
        for (Film film : sortedFilms) {
            filmListView.getItems().add(film.getTitle() + " (" + film.getreleaseYear() + ")");
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
