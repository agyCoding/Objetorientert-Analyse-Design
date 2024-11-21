// Last Modified: 30.09.2024
package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.CategoryManager;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.models.Category;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.customcells.CategoryCell;
import com.oap2024team7.team7mediastreamingapp.customcells.CustomerFilmCell;
import com.oap2024team7.team7mediastreamingapp.customcells.RatingCell;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;

/**
 * Controller class for the primary screen.
 * It displays a list of films and allows the user to filter and sort the films.
 * The controller also allows the user to view film details and navigate to the edit profile screen or logout.
 * @author  Agata (Agy) Olaussen (@agyCoding)
 */

public class PrimaryController {

    // User menu
    @FXML
    private Label loggedInUserLabel;
    @FXML
    private MenuButton userAccountMenuButton;
    @FXML
    private MenuItem manageProfilesMenuItem;
    @FXML
    private MenuItem editAccountMenuItem;
    @FXML
    private MenuItem editProfileMenuItem;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private VBox filterMenu;

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
    private FilmManager filmManager;
    private int offset = 0;
    private final int limit = 20; // Load 20 films per page
    private CategoryManager categoryManager = new CategoryManager();
    private Customer loggedInCustomer;
    private Profile currentProfile;

    // Store filter criteria
    private Category selectedCategory;
    private Film.Rating selectedRating;
    private Integer selectedMaxLength;
    private Integer selectedStartYear;
    private Integer selectedEndYear;
    
    /**
     * Initializes the controller class.
     * It is used to initialize the controller and load the initial data.
     */
    @FXML
    public void initialize() {
        loggedInCustomer = SessionData.getInstance().getLoggedInCustomer();
        currentProfile = SessionData.getInstance().getCurrentProfile();

        /* INITIALIZE USER MENU */

        // Setting the logged-in username as empty
        updateLoggedInUserLabel();

        // Check if the current profile is the main profile
        if (currentProfile != null && currentProfile.isMainProfile()) {
            editAccountMenuItem.setVisible(true);  // Show the Edit Account menu item if mainProfile = true
        } else {
            editAccountMenuItem.setVisible(false);  // Hide the Edit Account menu item if mainProfile = false
        }        
    
        // Handle manage profiles action
        manageProfilesMenuItem.setOnAction(event -> handleManageProfiles());

        // Handle edit account action
        editAccountMenuItem.setOnAction(event -> handleEditAccount());
    
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

        // Load the first page of films
        loadFilms();

        // Indicate how to present films in the LV (show only some information from Film class)
        filmListView.setCellFactory(lv -> new CustomerFilmCell());

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

    /**
     * Updates the logged-in user label with the current profile name,
     * and shows/hides the edit account menu item based on the current profile (if it's main profile or not).
     */
    private void updateLoggedInUserLabel() {
        currentProfile = SessionData.getInstance().getCurrentProfile();
        String profileName = currentProfile.getProfileName();
        loggedInUserLabel.setText("Logged in as: " + profileName);
    
        // Check if the current profile is the main profile and show/hide the editAccountMenuItem
        if (currentProfile != null && currentProfile.isMainProfile()) {
            editAccountMenuItem.setVisible(true);
        } else {
            editAccountMenuItem.setVisible(false);
        }
    }

    /**
     * Reloads the user data when the user logs in or changes the profile.
     */
    public void reloadUserData() {
        updateLoggedInUserLabel();
        loadFilms();
    }    

    /**
     * Changes scene when the user clicks the "Manage Profiles" menu item.
     */
    private void handleManageProfiles() {
        StageUtils.switchScene(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "manageProfiles",
            "Streamify - Manage Profiles"
        );
    }
   
    /**
     * Opens pop-up with Edit Account when the user clicks the "Edit Account" menu item.
     */
    @FXML
    private void handleEditAccount() {
        StageUtils.showPopup(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "editAccount",
            "Streamify - Edit Account",
            Modality.WINDOW_MODAL
        );
    }

    @FXML
    private void handleMyList() {
        StageUtils.showPopup(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "myList",
            "Streamify - My List",
            Modality.APPLICATION_MODAL
        );
    }
    
    /**
     * Opens pop-up with Edit Profile when the user clicks the "Edit Profile" menu item.
     */
    private void handleEditProfile() {
        System.out.println("Edit Profile clicked");
    
        // Call showPopup with the primary controller reference
        StageUtils.showPopup(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "editProfile",
            "Streamify - Edit Profile",
            Modality.WINDOW_MODAL,
            this  // Pass the reference of PrimaryController
        );
    }
    
    /**
     * Logs out the user and switches to the login screen.
     */
    private void handleLogout() {
        // Logic to log out the user
        System.out.println("Logout clicked");
        SessionData.getInstance().clearSessionData();
        switchToLogin();
    }

    /**
     * Loads films into the LV based on the selected filters, sort criteria, customer's storeId and pagination.
     */
    private void loadFilms() {

        int customersStoreId = loggedInCustomer.getStoreId();

        // Get sort criteria
        String sortBy = sortByTitle.isSelected() ? "title" : "release_year";

        // Check if the selectedCategory is the placeholder (-1); if so, treat it as null
        Integer categoryId = (selectedCategory != null && selectedCategory.getCategoryId() != -1) 
        ? selectedCategory.getCategoryId() 
        : null;
    
        // For rating: If selectedRating is null, treat it as no filter for the rating
        Film.Rating rating = selectedRating == Film.Rating.NONE ? null : selectedRating;

        List<Film> films = filmManager.loadFilms(
            categoryId,
            rating,
            selectedMaxLength,
            selectedStartYear,
            selectedEndYear,
            offset,
            limit,
            customersStoreId,
            sortBy
        );
    
        // Clear the film list view and populate with filtered results
        filmListView.getItems().clear();
        filmListView.getItems().addAll(films);
    
        // Check if there are more films to load for pagination
        if (films.size() < limit || filmManager.getAllFilms(offset + limit, limit, customersStoreId, sortBy).isEmpty()) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
    
        // Disable the previous button if on the first page
        prevButton.setDisable(offset == 0);
    }

    // Pagination methods
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

    /**
     * Updates the current page label with the current page number.
     */
    private void updateCurrentPageLabel() {
        int currentPage = (offset / limit) + 1;
        currentPageLabel.setText("Page: " + currentPage);
    }

    /**
     * Sorts the films based on the selected criteria.
     */
    @FXML    
    private void sortFilms() {
        loadFilms();
    }

    /**
     * Shows the details of the selected film in a pop-up window.
     * @param film
     */
    private void showFilmDetails(Film film) {
        SessionData.getInstance().setSelectedFilm(film);
        StageUtils.showPopup(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "filmDetails",  // Using the short name for the FXML file
            "Streamify - Film Details",
            Modality.WINDOW_MODAL  // Specify the modality
        );
    }

    /**
     * Load all categories and add them to the ComboBox.
     */
    private void loadCategories() {
        List<Category> categories = categoryManager.getAllCategories();
    
        if (categories == null) {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load categories", "An error occurred while trying to load the categories");
            return;
        } else {
            genreComboBox.getItems().clear();
            
            // Add a null option for the empty category
            Category placeholder = new Category(-1, "");
            genreComboBox.getItems().add(placeholder);
    
            // Set custom cells for displaying category names
            genreComboBox.setButtonCell(new CategoryCell());
            genreComboBox.setCellFactory(lv -> new CategoryCell());
    
            // Add categories to the ComboBox
            genreComboBox.getItems().addAll(categories);
    
            // Add a listener to update selectedCategory when the user changes the selection
            genreComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                // If the user selects the empty option, set selectedCategory to null
                if (newValue == null) {
                    selectedCategory = null;
                } else {
                    selectedCategory = newValue;
                }
            });
        }
    }
    
    
    /**
     * Load all ratings and add them to the ComboBox.
     */
    private void loadRatings() {
        // Get all the enum values, but do not include 'NONE' manually here
        Film.Rating[] ratings = Film.Rating.values();
    
        ratingComboBox.getItems().clear();
    
        // Add all the enum values to the ComboBox
        ratingComboBox.getItems().addAll(ratings);
    
        // Set custom cells for displaying rating names
        ratingComboBox.setCellFactory(lv -> new RatingCell());
        ratingComboBox.setButtonCell(new RatingCell());
    
        // Set the ComboBox value to 'NONE' initially, acting as the empty option
        ratingComboBox.setValue(Film.Rating.NONE);
    
        // Add listener to handle the selection
        ratingComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // If the selected value is 'NONE', set selectedRating to null (empty selection)
            if (newValue == Film.Rating.NONE) {
                selectedRating = null;  // Reset the selectedRating to null
            } else {
                selectedRating = newValue;  // Set the selectedRating to the valid selected value
            }
        });
    }
       

    /**
     * Apply the filters selected by the user and load the films.
     */
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
    
    /**
     * Clear all filters and load all films.
     */
    @FXML
    private void clearFilters() {
        genreComboBox.getSelectionModel().clearSelection();
        ratingComboBox.getSelectionModel().clearSelection();
        maxLengthField.clear();
        startYearField.clear();
        endYearField.clear();
    
        // Reset offset when clearing filters
        offset = 0;

        // Set filters to null
        applyFilters();
    
        // Load films with no filters applied
        loadFilms();

        // Update page label
        updateCurrentPageLabel();
    }

    /**
     * Switch to the login screen.
     */
    @FXML
    private void switchToLogin() {
        StageUtils.switchScene(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "login",
            "Streamify - Login"
        );
    }

    /**
     * Handle the My Rentals button action.
     * Opens the My Rentals screen.
     */
    @FXML
    public void handleMyRentalsButtonAction() {
        StageUtils.showPopup(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "myRentals",
            "Streamify - My Rentals",
            Modality.APPLICATION_MODAL
        );
    }

}
