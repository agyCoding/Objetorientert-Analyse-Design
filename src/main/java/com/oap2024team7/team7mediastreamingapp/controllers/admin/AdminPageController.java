// Last Modified: 11.11.2024
package com.oap2024team7.team7mediastreamingapp.controllers.admin;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.CategoryManager;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.models.Category;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Staff;
import com.oap2024team7.team7mediastreamingapp.customcells.AdminFilmCell;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;

import java.util.ArrayList;
import java.util.List;
import java.lang.System;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
 * Controller class for the admin page
 * This class is responsible for handling the user interactions and updating the UI for the admin page.
 * @author  Agata (Agy) Olaussen (@agyCoding)
 */

public class AdminPageController {

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
    @FXML
    private Button deleteButton;

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
    private Staff loggedInStaff;
    private List<Film> selectedFilms = new ArrayList<>();

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
        loggedInStaff = SessionData.getInstance().getLoggedInStaff();

        /* INITIALIZE USER MENU */

        // Setting the logged-in username as empty
        updateLoggedInUserLabel();

        // Handle edit account action
        editAccountMenuItem.setOnAction(event -> handleEditAdminAccount());

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

        // Set default selection
        sortByTitle.setSelected(true);

        // Load the first page of films
        loadFilms();

        // Indicate how to present films in the LV (show only some information from Film class)
        filmListView.setCellFactory(listView -> new AdminFilmCell(this));

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
        String staffName = loggedInStaff.getUsername();
        loggedInUserLabel.setText("Logged in as: " + staffName);
    }

    public void reloadUserData() {
        updateLoggedInUserLabel();
        loadFilms();
    }    
   
    // Handles the action when the user clicks the "Edit Account" menu item.
    private void handleEditAccount() {
        StageUtils.showPopup(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "editAdminAccount",
            "Streamify - Edit Admin Account",
            Modality.WINDOW_MODAL
        );
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
        int staffsStoreId = loggedInStaff.getStoreId();

        // Get sort criteria
        String sortBy = sortByTitle.isSelected() ? "title" : "release_year";

        List<Film> films = filmManager.loadFilms(
            selectedCategory != null ? selectedCategory.getCategoryId() : null,
            selectedRating,
            selectedMaxLength,
            selectedStartYear,
            selectedEndYear,
            offset,
            limit,
            staffsStoreId,
            sortBy
        );
    
        // Clear the film list view and populate with filtered results
        filmListView.getItems().clear();
        filmListView.getItems().addAll(films);
    
        // Check if there are more films to load for pagination
        if (films.size() < limit || filmManager.getAllFilms(offset + limit, limit, staffsStoreId, sortBy).isEmpty()) {
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
            "adminFilmManagement",
            "Streamify - Manage A Film",
            Modality.APPLICATION_MODAL
        );
    }

    // Load all categories and add them to the ComboBox
    private void loadCategories() {
        List<Category> categories = categoryManager.getAllCategories();
    
        if (categories == null) {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load categories", "An error occurred while trying to load the categories");
            return;
        } else {
            genreComboBox.getItems().clear();
            // Add a null option for the empty category
            genreComboBox.getItems().add(null);  // Displayed as an empty option
    
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
    
    // Load all ratings and add them to the ComboBox
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

    // Apply filters to the films list view
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
    
    //Clear all filters and reload the films
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

    // Method to handle checkbox selection
    public void notifyFilmSelected(Film film) {
        if (!selectedFilms.contains(film)) {
            selectedFilms.add(film);
            updateDeleteButtonVisibility();
        }
    }

    public void notifyFilmDeselected(Film film) {
        selectedFilms.remove(film);
        updateDeleteButtonVisibility();
    }

    private void updateDeleteButtonVisibility() {
        deleteButton.setVisible(!selectedFilms.isEmpty());
    }

    /**
     * Deletes the selected films from the database.
     * If a film is currently rented, it cannot be deleted.
     * If a film is deleted, it is removed from the list view.
     */
    @FXML
    private void deleteSelectedFilms() {
        if (selectedFilms.isEmpty()) {
            GeneralUtils.showAlert(AlertType.WARNING, "No Selection", "No films selected", "Please select at least one film to delete.");
            return;
        }

        List<Film> filmsToDelete = new ArrayList<>(selectedFilms);
        for (Film film : filmsToDelete) {
            try {
                boolean deleted = filmManager.deleteFilm(film);
                if (!deleted) {
                    GeneralUtils.showAlert(AlertType.ERROR, "Error", "Could not delete film", "The film '" + film.getTitle() + "' is currently rented.");
                } else {
                    selectedFilms.remove(film);
                    GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Film deleted", "The film '" + film.getTitle() + "' has been deleted.");
                }
            } catch (Exception e) {
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "An error occurred", "An error occurred while trying to delete the film '" + film.getTitle() + "'.");
                e.printStackTrace();
            }
        }
    
        // Refresh the film list
        loadFilms();
        selectedFilms.clear(); // Clear selection after deletion
        updateDeleteButtonVisibility(); // Update delete button visibility
    }

    /**
     * Method to change to the manage movies screen.
     * Reloads the current stage.
     */
    @FXML
    private void changeToManageMovies() {
        StageUtils.switchScene(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "adminPage",
            "Streamify - Manage Movies"
        );
    }

    /**
     * Method to change to the manage users screen.
     */
    @FXML
    private void changeToManageUsers() {
        StageUtils.switchScene(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "adminUserManagement",
            "Streamify - Manage Users"
        );
    }

    /**
     * Method to display a pop-up window for adding a new movie
     */
    @FXML
    private void tryToAddMovie() {
        StageUtils.showPopup(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "adminAddFilm",
            "Streamify - Add A New Film",
            Modality.WINDOW_MODAL
        );
    }

    // Redirect to the login screen
    @FXML
    private void switchToLogin() {
        StageUtils.switchScene(
            (Stage) loggedInUserLabel.getScene().getWindow(),
            "login",
            "Streamify - Login"
        );
    }
    

}
