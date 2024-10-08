// Last Modified: 07.10.2024
package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.CategoryManager;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.models.Category;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Staff;
import com.oap2024team7.team7mediastreamingapp.customcells.AdminFilmCell;

import java.io.IOException;
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
        editAccountMenuItem.setOnAction(event -> handleEditAccount());

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
        // Load the edit account screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editaccount.fxml"));
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

    // Load films based on the current filters
    private void loadFilms() {
        List<Film> films;
        int staffsStoreId = loggedInStaff.getStoreId();
    
        // Check if filters are applied
        if (selectedCategory != null || selectedRating != null || selectedMaxLength != null || selectedStartYear != null || selectedEndYear != null) {
            // If filters are applied, use the filterFilms method
            Integer categoryId = selectedCategory != null ? selectedCategory.getCategoryId() : null;
            films = filmManager.filterFilms(categoryId, selectedRating, selectedMaxLength, selectedStartYear, selectedEndYear, offset, limit, staffsStoreId);
        } else {
            // No filters applied, load all films
            films = filmManager.getAllFilms(offset, limit, staffsStoreId);
        }
    
        // Clear the film list view and populate with filtered results
        filmListView.getItems().clear();
        filmListView.getItems().addAll(films);
    
        // Check if there are more films to load for pagination
        if (films.size() < limit || filmManager.getAllFilms(offset + limit, limit, staffsStoreId).isEmpty()) {
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
        int staffsStoreId = loggedInStaff.getStoreId();

        if ("Sort by Title".equals(selectedSort)) {
            sortedFilms = filmManager.getFilmsSortedByTitle(staffsStoreId);
        } else {
            sortedFilms = filmManager.getFilmsSortedByYear(staffsStoreId);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfilmmanagement.fxml"));
            Parent root = loader.load();

            // Get the controller of the next scene
            AdminFilmManagementController controller = loader.getController();

            // Pass the customer object to the edit account controller
            controller.setSelectedFilm(film);

            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();
            popupStage.setTitle("Streamify - Manage film");

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

    // Redirect to the login screen
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
            // Show an error alert if the login screen cannot be loaded
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the login screen", "En error occured while trying to load the registration screen");
        }
    }

}
