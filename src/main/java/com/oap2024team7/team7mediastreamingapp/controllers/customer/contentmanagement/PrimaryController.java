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
import com.oap2024team7.team7mediastreamingapp.controllers.customer.accountmanagement.EditProfileController;

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

    public void reloadUserData() {
        updateLoggedInUserLabel();
        loadFilms();
    }    

    // Handles the action when the user clicks the "Manage profiles" menu item.
    private void handleManageProfiles() {
        System.out.println("Manage Profiles clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/accountmanagement/manageprofiles.fxml"));
            Parent root = loader.load();

            // Get the current stage from the loggedInUserLabel
            Stage currentStage = (Stage) loggedInUserLabel.getScene().getWindow();

            // Set the new scene for the current stage
            currentStage.setScene(new Scene(root));

            currentStage.setTitle("Streamify - Manage Profiles");

        } catch (IOException e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the manage profiles screen", "An error occurred while trying to load the manage profiles screen");
        }
    }
   
    // Handles the action when the user clicks the "Edit Account" menu item.
    private void handleEditAccount() {
        // Load the edit account screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/accountmanagement/editaccount.fxml"));
            Parent root = loader.load();

            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();
            popupStage.setTitle("Streamify - Edit Account");

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
    @FXML
    private void handleMyList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/contentmanagement/mylist.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("My List");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load My List", "An error occurred while trying to load My List.");
        }
    }
    
    // Handles the action when the user clicks the "Edit Profile" menu item.
    private void handleEditProfile() {
        System.out.println("Edit Profile clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/accountmanagement/editprofile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();

            // Pass the PrimaryController reference to EditProfileController
            editProfileController.setPrimaryController(this);

            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();
            popupStage.setTitle("Streamify - Edit Profile");

            // Set the scene for the pop-up stage
            popupStage.setScene(new Scene(root));

            // Make the pop-up window modal (blocks interaction with other windows until closed)
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(loggedInUserLabel.getScene().getWindow());

            // Show the pop-up window
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();

            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the edit profile screen", "An error occurred while trying to load the edit profile screen");
        }
    }
    
    // Handles the action when the user clicks the "Logout" menu item.
    private void handleLogout() {
        // Logic to log out the user
        System.out.println("Logout clicked");
        SessionData.getInstance().clearSessionData();
        switchToLogin();
    }

    // Load films based on the current filters
    private void loadFilms() {
        List<Film> films;
        int customersStoreId = loggedInCustomer.getStoreId();
    
        // Check if filters are applied
        if (selectedCategory != null || selectedRating != null || selectedMaxLength != null || selectedStartYear != null || selectedEndYear != null) {
            // If filters are applied, use the filterFilms method
            Integer categoryId = selectedCategory != null ? selectedCategory.getCategoryId() : null;
            films = filmManager.filterFilms(categoryId, selectedRating, selectedMaxLength, selectedStartYear, selectedEndYear, offset, limit, customersStoreId);
        } else {
            // No filters applied, load all films
            films = filmManager.getAllFilms(offset, limit, customersStoreId);
        }
    
        // Clear the film list view and populate with filtered results
        filmListView.getItems().clear();
        filmListView.getItems().addAll(films);
    
        // Check if there are more films to load for pagination
        if (films.size() < limit || filmManager.getAllFilms(offset + limit, limit, customersStoreId).isEmpty()) {
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

    // Update the current page label based on pagination
    private void updateCurrentPageLabel() {
        int currentPage = (offset / limit) + 1;
        currentPageLabel.setText("Page: " + currentPage);
    }

    // Sort films based on the selected option
    @FXML    
    private void sortFilms() {
        String selectedSort = sortComboBox.getValue();
        List<Film> sortedFilms;
        int customersStoreId = loggedInCustomer.getStoreId();

        if ("Sort by Title".equals(selectedSort)) {
            sortedFilms = filmManager.getFilmsSortedByTitle(customersStoreId);
        } else {
            sortedFilms = filmManager.getFilmsSortedByYear(customersStoreId);
        }
        filmListView.getItems().clear();
        filmListView.getItems().addAll(sortedFilms);
    }

    /**
     * Shows the details of the selected film in a pop-up window.
     * @param film
     */
    private void showFilmDetails(Film film) {
        // Load the edit account screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/contentmanagement/filmdetails.fxml"));
            Parent root = loader.load();

            // Get the controller of the next scene
            FilmDetailsController filmDetailsController = loader.getController();

            // Pass the customer object to the edit account controller
            filmDetailsController.setSelectedFilm(film);

            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();
            popupStage.setTitle("Streamify - Film Details");

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
            genreComboBox.getItems().add(null);  // Displayed as an empty option
    
            // OBS! Is it several places in the code? looks like a bug
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
        // Get all the enum values
        Film.Rating[] ratings = Film.Rating.values();

        ratingComboBox.getItems().clear();

        // Add a null option
        ratingComboBox.getItems().add(null);  // This will be displayed as an empty choice

        // Set custom cells for displaying rating names
        ratingComboBox.setCellFactory(lv -> new RatingCell());
        ratingComboBox.setButtonCell(new RatingCell());

        // Add all the enum values to the ComboBox
        ratingComboBox.getItems().addAll(ratings);
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

    /**
     * Handle the My Rentals button action.
     * Opens the My Rentals screen.
     */
    @FXML
    public void handleMyRentalsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/contentmanagement/myrentals.fxml"));
            Parent myRentalsRoot = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Streamify - My Rentals");
            stage.setScene(new Scene(myRentalsRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load My Rentals", "An error occurred while trying to load My Rentals.");
        }
    }

}
