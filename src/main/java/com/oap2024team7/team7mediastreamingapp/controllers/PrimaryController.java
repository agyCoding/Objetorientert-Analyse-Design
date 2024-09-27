/**
 * PrimaryController.java
 * 
 * Author(s): Team 7 - OAP 2024
 * Contributions:
 * - Agata: Implemented film loading, pagination, and filtering logic,
 *          Created the user menu system with login/logout and profile editing functionalities,
 *          Designed the filter and sorting functionality for films,
 *          Integrated film details viewing and layout management.
 * 
 * Purpose: 
 * The PrimaryController is responsible for managing the main interface of the media streaming and rental app. 
 * It handles the display of films, user login/logout, film filtering, and pagination for navigation between 
 * pages of film results. It also manages the user's profile options, including editing their profile and logging out.
 * 
 * The main tasks the controller addresses:
 * - Display a list of films, with support for sorting and filtering based on user-selected criteria.
 * - Support pagination so users can navigate through a list of films, loading a specific number of items per page.
 * - Enable users to toggle between filter menus and load the film details view for selected films.
 * - Allow users to log out or edit their profile information from the user menu.
 * 
 * Methods:
 * 
 * - `initialize()`: Initializes the controller by loading categories, films, user profile data, and setting up the 
 *   event listeners for the user menu and pagination controls.
 * 
 * - `loadFilms()`: Loads films either based on the applied filters or retrieves all films if no filters are set.
 *   It manages pagination, ensuring the correct number of films is displayed per page.
 * 
 * - `nextPage()`: Increments the offset for pagination and loads the next set of films.
 * 
 * - `previousPage()`: Decrements the offset for pagination and loads the previous set of films.
 * 
 * - `updateCurrentPageLabel()`: Updates the current page label to show the user which page they are on, based on
 *   the current offset and limit.
 * 
 * - `applyFilters()`: Applies user-selected filters for category, rating, year range, and film length. It resets
 *   the pagination to the first page and reloads the films according to the filter criteria.
 * 
 * - `handleEditProfile()`: Loads the profile editing interface where the user can update their account information.
 * 
 * - `handleLogout()`: Logs the user out of the application and redirects them to the login screen.
 * 
 * - `toggleFilterMenu()`: Toggles the visibility of the filter menu, allowing users to hide or show it.
 * 
 * - `showFilmDetails(String film)`: Displays more detailed information about a selected film when clicked.
 * 
 * - `loadCategories()`: Populates the genre filter dropdown with available categories.
 * 
 * - `loadRatings()`: Populates the rating filter dropdown with available film rating options.
 * 
 * - `switchToLogin()`: Redirects the user to the login screen after a logout action.
 */


package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.CategoryManager;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.models.Category;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;

public class PrimaryController {

    // User menu
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

    // Pirmary content viewer (LV)
    @FXML
    private ListView<Film> filmListView;
    @FXML
    private Label currentPageLabel;

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

    // Filters menu
    @FXML
    private ComboBox<Category> genreComboBox;
    @FXML
    private ComboBox<Film.Rating> ratingComboBox;
    @FXML
    private TextField maxLengthField;
    @FXML
    private TextField startYearField;
    @FXML
    private TextField endYearField;
    
    // Local variables
    private ToggleGroup sortToggleGroup;
    private String currentUsername;
    private FilmManager filmManager;
    private int offset = 0;
    private final int limit = 20; // Load 20 films per page
    private CategoryManager categoryManager = new CategoryManager();
    private Customer loggedInCustomer;

    // Store filter criteria
    private Category selectedCategory;
    private Film.Rating selectedRating;
    private Integer selectedMaxLength;
    private Integer selectedStartYear;
    private Integer selectedEndYear;
    
    @FXML
    public void initialize() {
        loggedInCustomer = SessionData.getInstance().getLoggedInCustomer();

        /* INITIALIZE USER MENU */

        // Example: setting the logged-in username
        loggedInUserLabel.setText("Logged in as: ");
    
        // Handle edit profile action
        editProfileMenuItem.setOnAction(event -> handleEditProfile());
    
        // Handle logout action
        logoutMenuItem.setOnAction(event -> handleLogout());

        /* INITIALIZE FILTERS FOR LV */

        loadCategories();
        loadRatings();

        /* INITIALIZE FILM LV */

        filmManager = new FilmManager();

        // Initialize the ToggleGroup in the controller
        sortToggleGroup = new ToggleGroup();
        
        // Assign the ToggleGroup to the RadioButtons
        sortByTitle.setToggleGroup(sortToggleGroup);
        sortByReleaseYear.setToggleGroup(sortToggleGroup);

        // Set default selection if needed
        sortByTitle.setSelected(true);

        toggleFilterMenuButton.setOnAction(event -> toggleFilterMenu());

        // Load the first page of films
        loadFilms();

        // Indicate how to present films in the LV (show only some information from Film class)
        filmListView.setCellFactory(lv -> new ListCell<Film>() {
            @Override
            protected void updateItem(Film film, boolean empty) {
                super.updateItem(film, empty);
                if (empty || film == null) {
                    setText(null);
                } else {
                    // Display the title and release year in the ListView
                    setText(film.getTitle() + " (" + film.getReleaseYear() + ")");
                }
            }
        });

        // Handle next and previous page
        nextButton.setOnAction(event -> nextPage());
        prevButton.setOnAction(event -> previousPage());

        filmListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Film selectedFilm = filmListView.getSelectionModel().getSelectedItem();
                if (selectedFilm != null) {
                    showFilmDetails(selectedFilm);
                }
            }
        });
    }

    public void setLoggedInUsername(String username) {
        currentUsername = username; // Example value
        loggedInUserLabel.setText("Logged in as: " + currentUsername);
    }
    
    private void handleEditProfile() {
        // Load the edit account screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editaccount.fxml"));
            Parent root = loader.load();

            // Get the controller of the next scene
            EditAccountController editAccountController = loader.getController();

            // Pass the customer object to the edit account controller
            editAccountController.setLoggedInCustomer(loggedInCustomer);

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

        // Logic to edit the profile
        System.out.println("Edit Profile clicked");
    }
    
    private void handleLogout() {
        // Logic to log out the user
        System.out.println("Logout clicked");
        SessionData.getInstance().clearSessionData();
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
        List<Film> films;
    
        // Check if filters are applied
        if (selectedCategory != null || selectedRating != null || selectedMaxLength != null || selectedStartYear != null || selectedEndYear != null) {
            // If filters are applied, use the filterFilms method
            Integer categoryId = selectedCategory != null ? selectedCategory.getCategoryId() : null;
            films = filmManager.filterFilms(categoryId, selectedRating, selectedMaxLength, selectedStartYear, selectedEndYear, offset, limit);
        } else {
            // No filters applied, load all films
            films = filmManager.getAllFilms(offset, limit);
        }
    
        // Clear the film list view and populate with filtered results
        filmListView.getItems().clear();
        filmListView.getItems().addAll(films);
    
        // Check if there are more films to load for pagination
        if (films.size() < limit || filmManager.getAllFilms(offset + limit, 1).isEmpty()) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
    
        // Disable the previous button if on the first page
        prevButton.setDisable(offset == 0);
    }

    private void nextPage() {
        offset += limit;
        loadFilms();
        updateCurrentPageLabel();
    }

    private void previousPage() {
        if (offset > 0) {
            offset -= limit;
            loadFilms();
            updateCurrentPageLabel();
        }
    }

    private void updateCurrentPageLabel() {
        int currentPage = (offset / limit) + 1;
        currentPageLabel.setText("Page: " + currentPage);
    }

    @FXML    
    private void sortFilms() {
        String selectedSort = sortComboBox.getValue();
        List<Film> sortedFilms;
        if ("Sort by Title".equals(selectedSort)) {
            sortedFilms = filmManager.getFilmsSortedByTitle();
        } else {
            sortedFilms = filmManager.getFilmsSortedByYear();
        }
        filmListView.getItems().clear();
        filmListView.getItems().addAll(sortedFilms);
    }

    private void showFilmDetails(Film film) {
        // Load the edit account screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmdetails.fxml"));
            Parent root = loader.load();

            // Get the controller of the next scene
            FilmDetailsController filmDetailsController = loader.getController();

            // Pass the customer object to the edit account controller
            filmDetailsController.setSelectedFilm(film);

            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();
            popupStage.setTitle("Media Streaming and Rental - Film Details");

            // Set the scene for the pop-up stage
            popupStage.setScene(new Scene(root));

            popupStage.initOwner(loggedInUserLabel.getScene().getWindow());

            // Show the pop-up window
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();

            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the page with film details", "An error occurred while trying to load the film details page.");
        }
        // Small check in terminal (Debugging)
        System.out.println("Film details: " + film.getTitle() + " (" + film.getReleaseYear() + ")");
    }

    private void loadCategories() {
        List<Category> categories = categoryManager.getAllCategories();
    
        if (categories == null) {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load categories", "An error occurred while trying to load the categories");
            return;
        } else {
            genreComboBox.getItems().clear();
            // Add a null option
            genreComboBox.getItems().add(null);  // This will be displayed as an empty choice
            
            // Set how the selected category will be displayed in the button area
            genreComboBox.setButtonCell(new ListCell<Category>() {
                @Override
                protected void updateItem(Category category, boolean empty) {
                    super.updateItem(category, empty);
                    setText(empty || category == null ? "" : category.getCategoryName());
                }
            });
    
            // Set the display for ComboBox to show the categoryName instead of the whole object
            genreComboBox.setCellFactory(lv -> new ListCell<Category>() {
                @Override
                protected void updateItem(Category category, boolean empty) {
                    super.updateItem(category, empty);
                    setText(empty || category == null ? "" : category.getCategoryName());
                }
            });
    
            // Add categories to the ComboBox
            genreComboBox.getItems().addAll(categories);
        }
    }
    
    private void loadRatings() {
        // Get all the enum values
        Film.Rating[] ratings = Film.Rating.values();

        ratingComboBox.getItems().clear();

        // Add a null option
        ratingComboBox.getItems().add(null);  // This will be displayed as an empty choice

        // Set how the rating will be displayed in the dropdown
        ratingComboBox.setCellFactory(lv -> new ListCell<Film.Rating>() {
            @Override
            protected void updateItem(Film.Rating rating, boolean empty) {
                super.updateItem(rating, empty);
                setText(empty || rating == null ? "" : rating.name());
            }
        });

        // Set how the selected rating is displayed in the ComboBox button
        ratingComboBox.setButtonCell(new ListCell<Film.Rating>() {
            @Override
            protected void updateItem(Film.Rating rating, boolean empty) {
                super.updateItem(rating, empty);
                setText(empty || rating == null ? "" : rating.name());
            }
        });

        // Add all the enum values to the ComboBox
        ratingComboBox.getItems().addAll(ratings);
    }

    @FXML
    private void applyFilters() {
        // Store the filters globally
        selectedCategory = genreComboBox.getSelectionModel().getSelectedItem();
        selectedRating = ratingComboBox.getSelectionModel().getSelectedItem();
        selectedMaxLength = maxLengthField.getText().isEmpty() ? null : Integer.parseInt(maxLengthField.getText());
        selectedStartYear = startYearField.getText().isEmpty() ? null : Integer.parseInt(startYearField.getText());
        selectedEndYear = endYearField.getText().isEmpty() ? null : Integer.parseInt(endYearField.getText());
    
        // Reset offset when applying filters
        offset = 0;
    
        // Load films with the current filters applied
        loadFilms();

        // Update page label
        updateCurrentPageLabel();
    }
        
    @FXML
    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) loggedInUserLabel.getScene().getWindow();
            stage.setTitle("Media Streaming and Rental - Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the login screen", "En error occured while trying to load the registration screen");
        }
    }

}
