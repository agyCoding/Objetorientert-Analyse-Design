// Last Modified: 11.11.2024
package com.oap2024team7.team7mediastreamingapp.controllers.admin;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import java.util.List;
import java.util.HashSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.oap2024team7.team7mediastreamingapp.customcells.CategoryCell;
import com.oap2024team7.team7mediastreamingapp.customcells.LanguageCell;
import com.oap2024team7.team7mediastreamingapp.models.Category;
import com.oap2024team7.team7mediastreamingapp.models.Language;
import com.oap2024team7.team7mediastreamingapp.customcells.RatingCell;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.services.CategoryManager;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.services.LanguageManager;
import com.oap2024team7.team7mediastreamingapp.models.Actor;
import com.oap2024team7.team7mediastreamingapp.models.Staff;
import com.oap2024team7.team7mediastreamingapp.services.InventoryManager;
import com.oap2024team7.team7mediastreamingapp.models.Inventory;
import com.oap2024team7.team7mediastreamingapp.customcells.AdminActorCell;
import com.oap2024team7.team7mediastreamingapp.customcells.AdminSpecialFeaturesCell;
import com.oap2024team7.team7mediastreamingapp.services.ActorManager;
import com.oap2024team7.team7mediastreamingapp.customcells.ActorComboBoxCell;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.services.DatabaseManager;
import com.oap2024team7.team7mediastreamingapp.models.Discount;
import com.oap2024team7.team7mediastreamingapp.services.DiscountManager;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;


/**
 * Controller class for the Film Management window in the admin screen.
 * This class is responsible for handling the user interactions and updating the UI for the page that allows an admin to edit information about an existing movie.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class AdminFilmManagementController {
    @FXML
    private Button updateButton;

    // Basic film information from FXML
    @FXML
    private TextField filmTitleTF;
    @FXML
    private TextArea descriptionTA;
    @FXML
    private TextField releaseYearTF;
    @FXML
    private ComboBox<Category> categoryCB;
    @FXML
    private ComboBox<Film.Rating> pgRatingCB;
    @FXML
    private ComboBox<Language> languageCB;
    @FXML
    private TextField lengthTF;

    // Special features and actors from FXML
    @FXML
    private ComboBox<String> specialfeaturesCB;
    @FXML
    private ListView<String> specialFeaturesLV;
    @FXML
    private Button deleteSelectedSFButton;
    @FXML
    private ComboBox<Actor> actorsCB;
    @FXML
    private ListView<Actor> actorsLV;
    @FXML
    private Button deleteSelectedActorsButton;

    // Rental information from FXML
    @FXML
    private TextField rentalDurationTF;
    @FXML
    private TextField rentalRateTF;

    // Inventory information from FXML
    @FXML
    private Label currentInventoryLabel;
    @FXML
    private TextField inventoryAmountTF;

    // Discount information from FXML
    @FXML
    private TextField discountPercentageTF;
    @FXML
    private DatePicker discountEndDateDP;

    // Information about enabling different features
    @FXML
    private CheckBox likeDislikeCheckBox;
    @FXML
    private CheckBox enableReviewCheckBox;
    @FXML
    private CheckBox enableFreeCheckBox;

    // Local variables
    private Film selectedFilm;
    private Stage stage;
    private int currentInventorySize;
    private List<Actor> selectedActors = new ArrayList<>();
    private List<String> selectedSpecialFeatures = new ArrayList<>();
    private CategoryManager categoryManager = new CategoryManager();
    private ActorManager actorManager = ActorManager.getInstance();
    private InventoryManager inventoryManager = new InventoryManager();
    private FilmManager filmManager = new FilmManager();
    private DiscountManager discountManager = new DiscountManager();
        
    // Store information about chosen inputs
    private Category selectedCategory;
    private String selectedSpecialFeature;
    @SuppressWarnings("unused")
    private Language selectedLanguage = null;


    public void initialize() {   
        // Retrieve the selected film from the session data
        selectedFilm = SessionData.getInstance().getSelectedFilm();

        loadCategories();
        loadRatings();
        loadLanguages();
        loadSpecialFeatures();
        loadActors();

        if (selectedFilm != null) {
            updateFilmDetails();
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Film not found", "Unable to retrieve the selected film.");
        }
    }

    /**
     * The stage for the Film Details window.
     * This is used to set a listener to clear the selected film when the window is closed.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        // Set a listener to clear the selectedFilm when the window is closed
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // Clear the selected film from SessionData
                SessionData.getInstance().setSelectedFilm(null);
            }
        });
    }

    /**
     * Method to update the film details in the admin screen.
     * It pre-sets the text fields and ComboBoxes to the values of the selected film (based on the info from the DB).
     */
    
    private void updateFilmDetails() {
        filmTitleTF.setText(selectedFilm.getTitle());
        descriptionTA.setText(selectedFilm.getDescription());
        releaseYearTF.setText(String.valueOf(selectedFilm.getReleaseYear()));
        lengthTF.setText(String.valueOf(selectedFilm.getLength()));
        rentalDurationTF.setText(String.valueOf(selectedFilm.getRentalDuration()));
        rentalRateTF.setText(String.valueOf(selectedFilm.getRentalRate()));
        enableFreeCheckBox.setSelected(selectedFilm.isStreamable());
        likeDislikeCheckBox.setSelected(selectedFilm.isRatable());
        enableReviewCheckBox.setSelected(selectedFilm.isReviewable());

        // Set the discount information
        Discount discount = discountManager.getActiveDiscount(selectedFilm.getFilmId());
        if (discount != null) {
            discountPercentageTF.setText(String.valueOf(discount.getDiscountPercentage()));
            discountEndDateDP.setValue(discount.getEndDate());
        }

        // Set the current inventory label
        InventoryManager inventoryManager = new InventoryManager();
        Staff staff = SessionData.getInstance().getLoggedInStaff();
        List<Inventory> currentInventory = inventoryManager.checkInventoryForFilmAndStore(selectedFilm, staff.getStoreId());
        currentInventorySize = currentInventory.size();
        
        currentInventoryLabel.setText("Current inventory: " + currentInventorySize);
        inventoryAmountTF.setText(String.valueOf(currentInventorySize));

        // Preselect the film's rating
        if (selectedFilm.getRating() != null) {
            pgRatingCB.setValue(selectedFilm.getRating());
        }

        // Preselect the film's language
        if (selectedFilm.getLanguage() != null) {
            Language selectedFilmLanguage = selectedFilm.getLanguage();
            // Find the matching language in the ComboBox list
            for (Language language : languageCB.getItems()) {
                if (language.getLanguageId() == selectedFilmLanguage.getLanguageId()) {
                    languageCB.setValue(language);
                    break;
                }
            }
        }

        // Pre-select the category for the current film
        selectedCategory = categoryManager.getCategoryByFilmId(selectedFilm.getFilmId());
        if (selectedCategory != null) {
            for (Category category : categoryCB.getItems()) {
                if (category.getCategoryId() == selectedCategory.getCategoryId()) {
                    categoryCB.setValue(category); // Match by ID
                    break;
                }
            }
        } else {
            System.out.println("No category loaded");
        }
        loadCurrentActors();
        loadCurrentSpecialFeatures();
    }

    /**
     * Loads all categories possible to choose from (from the database) and populates ComboBox (with category objects).
     * Display is steered by the Customer Cell factory.
     * Updated attribute selectedCategory based on the choice made in the ComboBox.
     */
    private void loadCategories() {
        List<Category> categories = categoryManager.getAllCategories();
        categoryCB.getItems().clear();

        if (categories == null) {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load categories", "An error occurred while trying to load the categories");
            return;
        } else {
            categoryCB.getItems().clear();

            categoryCB.setPromptText("Select a category");  // Set a default prompt text   

            // Set custom cells for displaying category names
            categoryCB.setButtonCell(new CategoryCell());
            categoryCB.setCellFactory(lv -> new CategoryCell());
    
            // Add categories to the ComboBox
            categoryCB.getItems().addAll(categories);
    
            // Add a listener to update selectedCategory when the user changes the selection
            categoryCB.valueProperty().addListener((observable, oldValue, newValue) -> {
                    selectedCategory = newValue;
            });
        }
    }

    // Load all ratings and add them to the ComboBox
    private void loadRatings() {
        // Get all the enum values
        Film.Rating[] ratings = Film.Rating.values();

        pgRatingCB.getItems().clear();

        // Set custom cells for displaying rating names
        pgRatingCB.setCellFactory(lv -> new RatingCell());
        pgRatingCB.setButtonCell(new RatingCell());

        // Add all the enum values to the ComboBox
        pgRatingCB.getItems().addAll(ratings);
    }

    /**
     * Loads all possible languages to choose from into the ComboBox and listens to the changes made by the staff member.
     * Display is steered by the Custom Cell factory.
     * Updates attribute selectedLanguage to the choice selected in the ComboBox.
     */
    private void loadLanguages() {
        LanguageManager languageManager = new LanguageManager();
        List<Language> languages = languageManager.getAllLanguages();

        if (languages == null) {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load languages", "An error occurred while trying to load the languages");
            return;
        }

        languageCB.getItems().clear();

        languageCB.setPromptText("Select a language");  // Set a default prompt text         

        // Set custom cells for displaying language names
        languageCB.setButtonCell(new LanguageCell());
        languageCB.setCellFactory(lv -> new LanguageCell());

        languageCB.getItems().addAll(languages);

        // Add a listener to update selectedLanguage when the user changes the selection
        languageCB.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedLanguage = newValue;
        });
    }
    
    /**
     * Loads the list of all possibile special features to choose from, into the ComboBox
     */
    private void loadSpecialFeatures() {
        // Clear existing items
        specialfeaturesCB.getItems().clear();
    
        // Add predefined special features to the ComboBox
        specialfeaturesCB.getItems().addAll(Film.getPredefinedSpecialFeatures());
    }    

    /**
     * Loads current special features for the selected film into the LV
     */
    private void loadCurrentSpecialFeatures() {
        // Clear existing items
        specialFeaturesLV.getItems().clear();

        if (selectedFilm != null && selectedFilm.getSpecialFeatures() != null) {

            // Set the custm cells for displaying special features
            specialFeaturesLV.setCellFactory(lv -> new AdminSpecialFeaturesCell(this));

            // Add the special features of the selected film to the ListView
            specialFeaturesLV.getItems().addAll(selectedFilm.getSpecialFeatures());
        }
    }

    /**
     * Loads into the ComboBox all actors registered in the actor table in the database
     * The staff member can then choose an actor from this CB to be added to the selected film
     */
    private void loadActors() {
        // Clear existing items
        actorsCB.getItems().clear();
    
        ActorManager actorManager = ActorManager.getInstance();
        List<Actor> actors = actorManager.getAllActors();
    
        if (actors == null) {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load actors", "An error occurred while trying to load the actors");
            return;
        }
    
        // Add Actor objects directly to the ComboBox
        actorsCB.getItems().addAll(actors);
    
        // Use the custom cell class for displaying actors
        actorsCB.setCellFactory(lv -> new ActorComboBoxCell());
    
        // Set the display for ComboBox when an actor is selected
        actorsCB.setButtonCell(new ActorComboBoxCell());
    }    

    /**
     * Loads into the LV the set of current actors for the selected film
     */
    private void loadCurrentActors() {
        // Clear existing items
        actorsLV.getItems().clear();

        // Set custom cells for displaying actors
        actorsLV.setCellFactory(lv -> new AdminActorCell(this));

        if (selectedFilm != null && selectedFilm.getActors() != null) {
            // Add the actors of the selected film to the ListView
            actorsLV.getItems().addAll(selectedFilm.getActors());
        }
    }
    
    /**
     * Method to update the film, actors and inventory. Called when the staff member clicks on the "Update" button
     */
    @FXML
    public void tryToUpdateFilm() {
        System.out.println("Trying to update film...");
        // Get the values from the input fields related to the Film class
        String title = filmTitleTF.getText();
        String description = descriptionTA.getText();
        String releaseYear = releaseYearTF.getText();
        Film.Rating rating = pgRatingCB.getValue();
        Language language = languageCB.getValue();
        List<String> specialFeatures = specialFeaturesLV.getItems();
        List<Actor> actors = actorsLV.getItems();

        Category category = categoryCB.getValue();

        // int inventoryAmount = Integer.parseInt(inventoryAmountTF.getText());
        // int inventoryStore = inventoryStoreCB.getValue();

        // Check if all required fields are filled
        if (title.isEmpty() || language == null || category == null) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Missing information", "Please fill in all required fields.");
            return;
        }

        // Update Film object with the new values
        selectedFilm.setTitle(title);
        selectedFilm.setDescription(description);

        // Validate the release year and set it in the Film object
        try {
            int year = Integer.parseInt(releaseYear);
            if (year < 1888 || year > java.time.Year.now().getValue()) { // Validate year range
                GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Year", "Please enter a valid year.");
                return;
            }
            selectedFilm.setReleaseYear(year);
        } catch (NumberFormatException e) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Year", "Please enter a valid year.");
            return;
        }
        selectedFilm.setRating(rating);
        selectedFilm.setLanguage(language);

        // Validate the length and set it in the Film object
        try {
            int length = Integer.parseInt(lengthTF.getText());
            if (length < 1 || length > 600) { // Validate length range
                GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Length", "Please enter a valid length in minutes (1-600).");
                return;
            }
            selectedFilm.setLength(length);
        } catch (NumberFormatException e) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Length", "Please enter a valid length in minutes (1-600).");
            return;
        }
        selectedFilm.setSpecialFeatures(new HashSet<>(specialFeatures));
        selectedFilm.setActors(actors);
        selectedFilm.setStreamable(enableFreeCheckBox.isSelected());
        selectedFilm.setRatable(likeDislikeCheckBox.isSelected());
        selectedFilm.setReviewable(enableReviewCheckBox.isSelected());

        // Validate the rental duration and set it in the Film object
        try {
            int rentalDuration = Integer.parseInt(rentalDurationTF.getText());
            if (rentalDuration < 1 || rentalDuration > 365) { // Validate rental duration range
                GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Rental Duration", "Please enter a valid rental duration in days (1-365).");
                return;
            }
            selectedFilm.setRentalDuration(rentalDuration);
        } catch (NumberFormatException e) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Rental Duration", "Please enter a valid rental duration in days (1-365).");
            return;
        }

        // Validate the rental rate and set it in the Film object
        String rentalRateInput = rentalRateTF.getText().replace(',', '.'); // Replace comma with dot
        try {
            double rentalRate = Double.parseDouble(rentalRateInput);
            if (rentalRate < 0 || rentalRate >= 10000 || !rentalRateInput.matches("\\d{1,4}\\.\\d{1,2}")) { // Validate range and format
                GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Rental Rate", "Please enter a valid rental rate in the format 4,2 (e.g., 1234.56).");
                return;
            }
            selectedFilm.setRentalRate(rentalRate);
        } catch (NumberFormatException e) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Rental Rate", "Please enter a valid rental rate in the format 4,2 (e.g., 1234.56).");
            return;
        }

        boolean filmDetailsUpdated = updateFilmDetails(selectedFilm, category, actors);

        // Get feedback from the updates that need to happen and display appropriate message to the user
        if (filmDetailsUpdated) {
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success!", "Successfully edited selected film", "You've successfully edited selected film item.");
            refreshFilmData();
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Something went wrong", "Try again.");
        }
    }

    /**
     * Method for updating the film details in the database, in a transaction.
     * @param selectedFilm
     * @param category
     * @param actors
     * @return true if the update was successful, false otherwise
     */
    public boolean updateFilmDetails(Film selectedFilm, Category category, List<Actor> actors) {
        Connection connection = null;
        boolean success = false;
    
        try {
            // Obtain the database connection (assuming DatabaseManager handles this)
            connection = DatabaseManager.getConnection();
            connection.setAutoCommit(false); // Start transaction
    
            // Try to update the film in the database
            boolean filmUpdated = filmManager.updateFilm(selectedFilm);
            if (!filmUpdated) {
                throw new SQLException("Failed to update film");
            }
    
            // Try to update category in the database
            boolean categoryUpdated = categoryManager.updateCategoryForFilm(selectedFilm, category);
            if (!categoryUpdated) {
                throw new SQLException("Failed to update category for film");
            }
    
            // Try to update actors in the database
            boolean actorsUpdated = actorManager.setActorsForFilm(actors, selectedFilm.getFilmId());
            if (!actorsUpdated) {
                throw new SQLException("Failed to update actors for film");
            }
    
            // Inventory handling
            boolean inventoryUpdated = handleInventory();
            if (!inventoryUpdated) {
                throw new SQLException("Failed to update inventory");
            }
    
            connection.commit(); // Commit transaction if all operations succeed
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // Roll back transaction if any operation fails
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset to default auto-commit mode
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * Method for refreshing film data (querrying the database).
     * Used after the staff member have updated film information
     * (since the window doesn't close after update, it should show current information).
     */
    private void refreshFilmData() {
        if (selectedFilm != null) {
            selectedFilm = filmManager.getFilmById(selectedFilm.getFilmId());
            updateFilmDetails();
        }
    }

    /**
     * Method for handling inventory changes. It validates user input first (if it's the correct format and above 0),
     * then establishes if the user is trying to increase, decrease or not change the inventory and calls the correct method.
     * @return true if successful (in either of the 3 options) and false otherwise
     */
    private boolean handleInventory() {
        try {
            int inventoryAmount = Integer.parseInt(inventoryAmountTF.getText());
            if (inventoryAmount < 0) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Inventory Amount", "Please enter a valid inventory amount (0 or positive number).");
            return false;
            }

            int filmId = selectedFilm.getFilmId();
            int storeId = SessionData.getInstance().getLoggedInStaff().getStoreId();

            if (inventoryAmount > currentInventorySize) {
                return addInventory(filmId, storeId, inventoryAmount - currentInventorySize);
            } else if (inventoryAmount < currentInventorySize) {
                return reduceInventory(filmId, storeId, currentInventorySize - inventoryAmount);
            } else {
                // No change in inventory amount
                return true;
            }
        } catch (NumberFormatException e) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Inventory Amount", "Please enter a valid inventory amount (0 or positive number).");
            return false;
        }
    }

    /**
     * Method for adding new inventory items. Always allowed, no constraints.
     * @param filmId
     * @param storeId
     * @param amount
     * @return true if the inventory was successfully added, false otherwise
     */
    private boolean addInventory(int filmId, int storeId, int amount) {
        System.out.println("Adding inventory...");
        return inventoryManager.addInventoryForFilm(filmId, storeId, amount);
    }

    /**
     * Method for reducing the amount of inventory items for the selected film.
     * If the desired amount is not available, an alert will be shown.
     * The method will check if the inventory items are currently rented out and not allow the reduction in that case.
     * Staff gets feedback about the amount of successfully deleted inventory items (f. ex. if 2 out of desired 3 were deleted,
     * the staff will be informed).
     * @param filmId
     * @param storeId
     * @param amount
     * @return true if the inventory was successfully reduced, false otherwise
     */
    private boolean reduceInventory(int filmId, int storeId, int amount) {
        System.out.println("Decreasing inventory...");
        LocalDateTime now = LocalDateTime.now();
        List<Inventory> availableInventory = inventoryManager.checkForAvailableInventory(filmId, storeId, now, now);

        if (availableInventory.isEmpty()) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Unable to reduce the amount of inventory", "Inventory items are currently rented out. Please wait until they are returned.");
            return false;
        }

        int deletedItems = 0;
        for (Inventory inventory : availableInventory) {
            if (deletedItems >= amount) {
            break;
            }
            if (inventoryManager.removeRentalForInventory(inventory.getInventoryId()) && 
            inventoryManager.deleteAvailableInventory(inventory.getInventoryId())) {
            deletedItems++;
            } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Failed to delete inventory item " + inventory.getInventoryId() + ".", "Some inventory items might be currently rented out. Please wait until they are returned.");
            }
        }

        if (deletedItems > 0) {
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Successfully deleted " + deletedItems + " inventory items.", "");
            return true;
        } else {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Unable to delete the desired number of inventory items. Deleted " + deletedItems + " items.", "");
            return false;
        }
    }

    /**
     * Method for adding a new special feature to the list of special features connected to the selected film.
     * If the special feature is already in the list, an alert will be shown.
     */
    @FXML
    public void tryToAddSF() {
        System.out.println("Trying to add special feature...");
        // Get the selected special feature from the ComboBox
        selectedSpecialFeature = specialfeaturesCB.getValue();
        
        // Check if a special feature is selected and not already in the ListView
        if (selectedSpecialFeature != null && !specialFeaturesLV.getItems().contains(selectedSpecialFeature)) {
            // Add the selected special feature to the selectedFilm object
            selectedFilm.getSpecialFeatures().add(selectedSpecialFeature);
            // Add the selected special feature to the ListView
            specialFeaturesLV.getItems().add(selectedSpecialFeature);
        } else {
            // Show an alert if no special feature is selected or if it is already in the ListView
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Special Feature not added", "Please select a valid special feature that is not already added.");
        }
    }

    /**
     * Method for adding a new actor to the list of actors connected to the
     * selected film.
     * If the actor is already in the list, an alert will be shown.
     */
    @FXML
    public void tryToAddActor() {
        System.out.println("Trying to add actor...");
        // Get the selected actor from the ComboBox
        Actor selectedActor = actorsCB.getValue();

        // Check if an actor is selected and check if the selected actor is not already in the ListView
        if (selectedActor != null && !actorsLV.getItems().contains(selectedActor)) {
            // Add the selected actor to the selectedFilm object
            selectedFilm.getActors().add(selectedActor);
            // Add the selected actor to the ListView
            actorsLV.getItems().add(selectedActor);
        } else {
            // Show an alert if no actor is selected or if it is already in the ListView
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Actor not added", "Please select a valid actor that is not already added.");
        }
    }

    /**
     * Method for updating the visibility of the button that allows the staff
     * to delete selected actors from the selected film.
     * If no actor is selected via checkboxes, the button should be invisible.
     */
    private void updateDeleteActorsButtonVisibility() {
        deleteSelectedActorsButton.setVisible(!selectedActors.isEmpty());
    }

    /**
     * Method for updating (adding to) the list of selected actors and the visibility of the delete button.
     * @param actor
     */
    public void notifyActorSelected(Actor actor) {
        if (!selectedActors.contains(actor)) {
            selectedActors.add(actor);
            updateDeleteActorsButtonVisibility();
        }
    }

    /**
     * Method for updating (removing from) the list of selected actors and the visibility of the delete button.
     * @param actor
     */
    public void notifyActorDeselected(Actor actor) {
        selectedActors.remove(actor);
        updateDeleteActorsButtonVisibility();
    }

    /**
     * Method for deleting the selected actors from the selected film.
     */
    @FXML
    public void tryToDeleteSelectedActors() {
        for (Actor actor : selectedActors) {
            selectedFilm.getActors().remove(actor);
            actorsLV.getItems().remove(actor);
        }
        selectedActors.clear();
        updateDeleteActorsButtonVisibility();
    }

    /**
     * Method for updating the visibility of the button that allows the staff
     * to delete selected special features from the selected film.
     */
    private void updateDeleteSpecialFeaturesButtonVisibility() {
        deleteSelectedSFButton.setVisible(!selectedSpecialFeatures.isEmpty());
    }

    /**
     * Method for updating (adding to) the list of selected special features and the visibility of the delete button.
     * @param specialFeature
     */
    public void notifySpecialFeatureSelected(String specialFeature) {
        if (!selectedSpecialFeatures.contains(specialFeature)) {
            selectedSpecialFeatures.add(specialFeature);
            updateDeleteSpecialFeaturesButtonVisibility();
        }
    }

    /**
     * Method for updating (removing from) the list of selected special features and the visibility of the delete button.
     * @param specialFeature
     */
    public void notifySpecialFeatureDeselected(String specialFeature) {
        selectedSpecialFeatures.remove(specialFeature);
        updateDeleteSpecialFeaturesButtonVisibility();
    }

    /**
     * Method for deleting the selected special features from the selected film.
     */
    @FXML
    public void tryToDeleteSelectedSpecialFeatures() {
        for (String specialFeature : selectedSpecialFeatures) {
            selectedFilm.getSpecialFeatures().remove(specialFeature);
            specialFeaturesLV.getItems().remove(specialFeature);
        }
        selectedSpecialFeatures.clear();
        updateDeleteSpecialFeaturesButtonVisibility();
    }

    /**
     * Method for applying a discount to the selected film.
     * The discount percentage and end date are taken from the input fields.
     * If there is no active discount for the film, a new discount is created, else the existing discount is updated.
     */
    @FXML
    public void applyDiscount() {
        System.out.println("Applying discount...");

        // Define common alert messages
        String discountUpdatedMessage = "The discount has been updated for the film.";
        String discountAppliedMessage = "The discount has been successfully applied to the film.";
        String failedToApplyHeader = "Failed to Apply Discount";
        String failedToUpdateDiscountMessage = "There was an error updating the discount. Please try again.";
        String failedToApplyDiscountMessage = "There was an error applying the discount. Please try again.";

        String discountPercentage = discountPercentageTF.getText();
        LocalDate discountEndDate = discountEndDateDP.getValue();

        // Validate and normalize discount percentage
        String normalizedDiscountPercentage = GeneralUtils.normalizeDecimalFormat(discountPercentage, 4, 2);
        if (normalizedDiscountPercentage == null) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Discount Percentage", "Please enter a valid discount percentage in the format 4,2 (e.g., 12.34).");
            return;
        }

        // Validate discount end date
        if (discountEndDate == null) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Discount End Date", "Please select a valid discount end date.");
            return;
        }

        // Ensure the discount end date is not in the past
        if (discountEndDate.isBefore(LocalDate.now())) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Discount End Date", "Discount end date cannot be in the past.");
            return;
        }

        long discountDurationDays = ChronoUnit.DAYS.between(LocalDate.now(), discountEndDate);
        if (discountDurationDays > 365) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Discount Duration", "Please select a discount end date within one year from today.");
            return;
        }

        // Check for an existing active discount for the selected film
        Discount existingDiscount = null;
        try {
            existingDiscount = discountManager.getActiveDiscount(selectedFilm.getFilmId());
            if (existingDiscount == null) {
                // If no active discount is found, create a new one
                Discount newDiscount = new Discount(selectedFilm.getFilmId(), Double.parseDouble(normalizedDiscountPercentage), LocalDate.now(), discountEndDate);
                int discountId = discountManager.registerDiscount(newDiscount);
                if (discountId != -1) {
                    GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Discount Applied", discountAppliedMessage);
                } else {
                    GeneralUtils.showAlert(AlertType.ERROR, "Error", "Failed to Apply Discount", failedToApplyDiscountMessage);
                }
            } else {
                // There is an active discount for the film; check if the new expiry date overlaps
                if (discountEndDate.isBefore(existingDiscount.getEndDate())) {
                    // If the new expiry date is within the existing discount period, update the discount
                    existingDiscount.setDiscountPercentage(Double.parseDouble(normalizedDiscountPercentage));
                    existingDiscount.setEndDate(discountEndDate);
                    boolean success = discountManager.updateDiscount(existingDiscount);
                    if (success) {
                        GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Discount Updated", discountUpdatedMessage);
                    } else {
                        GeneralUtils.showAlert(AlertType.ERROR, "Error", "Failed to Update Discount", failedToUpdateDiscountMessage);
                    }
                } else {
                    // If the new expiry date is after the existing one, create a new discount
                    Discount newDiscount = new Discount(selectedFilm.getFilmId(), Double.parseDouble(normalizedDiscountPercentage), LocalDate.now(), discountEndDate);
                    int discountId = discountManager.registerDiscount(newDiscount);
                    if (discountId != -1) {
                        GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Discount Applied", discountAppliedMessage);
                    } else {
                        GeneralUtils.showAlert(AlertType.ERROR, "Error", failedToApplyHeader, failedToApplyDiscountMessage);
                    }
                }
            }
        } catch (Exception e) {
            // Handle any other exceptions (e.g., SQLException wrapped by your manager class)
            System.err.println("Error retrieving active discount: " + e.getMessage());
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Database Error", "An error occurred while retrieving the discount information. Please try again.");
            return; // Stop further execution
        }
    }


    
}
