package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import java.util.List;
import java.util.HashSet;
import java.time.LocalDateTime;

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
    private ComboBox<Actor> actorsCB;
    @FXML
    private ListView<Actor> actorsLV;

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

    // Local variables
    private Film selectedFilm;
    private Stage stage;
    private int currentInventorySize;
    private CategoryManager categoryManager = new CategoryManager();
    private ActorManager actorManager = ActorManager.getInstance();
    private InventoryManager inventoryManager = new InventoryManager();
    private FilmManager filmManager = new FilmManager();
        

    // Store information about chosen inputs
    private Category selectedCategory;
    private String selectedSpecialFeature;
    private Language selectedLanguage = null;


    public void initialize() {     
        loadCategories();
        loadRatings();
        loadLanguages();
        loadSpecialFeatures();
        loadActors();
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
     * This method is called when a film is clicked in the admin screen.
     * @param film The film object that was clicked.
     */
    public void setSelectedFilm(Film film) {
        // Set the film object to the film object that was clicked
        this.selectedFilm = film;

        // Save the selected film to the session data
        SessionData.getInstance().setSelectedFilm(selectedFilm);
        
        // Now that the film is set, update the labels with the film's details
        updateFilmDetails();
    }
    
    private void updateFilmDetails() {
        filmTitleTF.setText(selectedFilm.getTitle());
        descriptionTA.setText(selectedFilm.getDescription());
        releaseYearTF.setText(String.valueOf(selectedFilm.getReleaseYear()));
        lengthTF.setText(String.valueOf(selectedFilm.getLength()));
        rentalDurationTF.setText(String.valueOf(selectedFilm.getRentalDuration()));
        rentalRateTF.setText(String.valueOf(selectedFilm.getRentalRate()));

        // Set the current inventory label
        InventoryManager inventoryManager = new InventoryManager();
        Staff staff = SessionData.getInstance().getLoggedInStaff();
        List<Inventory> currentInventory = inventoryManager.checkInventoryForFilmAndStore(selectedFilm, staff);
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
            System.out.println("Selected category: " + selectedCategory.getCategoryName());
        } else {
            System.out.println("No category loaded");
        }
        loadCurrentActors();
        loadCurrentSpecialFeatures();
    }

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
    
    private void loadSpecialFeatures() {
        // Clear existing items
        specialfeaturesCB.getItems().clear();
    
        // Add predefined special features to the ComboBox
        specialfeaturesCB.getItems().addAll(Film.getPredefinedSpecialFeatures());
    }    

    private void loadCurrentSpecialFeatures() {
        // Clear existing items
        specialFeaturesLV.getItems().clear();

        if (selectedFilm != null && selectedFilm.getSpecialFeatures() != null) {

            // Set the custm cells for displaying special features
            specialFeaturesLV.setCellFactory(lv -> new AdminSpecialFeaturesCell());

            // Add the special features of the selected film to the ListView
            specialFeaturesLV.getItems().addAll(selectedFilm.getSpecialFeatures());
        }
    }

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
        System.out.println("Selected category: " + category.getCategoryId());

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

        // Try to update the film in the database
        boolean filmUpdated = filmManager.updateFilm(selectedFilm);

        // Try to update category in the database
        boolean categoryUpdated = categoryManager.updateCategoryForFilm(selectedFilm, category);

        // Try to update actors in the database
        boolean actorsUpdated = actorManager.setActorsForFilm(actors, selectedFilm.getFilmId());
    
        // Inventory handling
        boolean inventoryUpdated = handleInventory();

        if (filmUpdated && categoryUpdated && actorsUpdated && inventoryUpdated) {
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success!", "Successfully edited selected film", "You've successfully edited selected film item.");
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Something went wrong", "Try again.");
        }
    }

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
            } else {
                return reduceInventory(filmId, storeId, currentInventorySize - inventoryAmount);
            }
        } catch (NumberFormatException e) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid Inventory Amount", "Please enter a valid inventory amount (0 or positive number).");
            return false;
        }
    }

    private boolean addInventory(int filmId, int storeId, int amount) {
        System.out.println("Adding inventory...");
        return inventoryManager.addInventoryForFilm(filmId, storeId, amount);
    }

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
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Failed to delete inventory item " + inventory.getInventoryId() + ".", "");
            return false;
            }
        }

        if (deletedItems == amount) {
            GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Successfully deleted " + deletedItems + " inventory items.", "");
            return true;
        } else {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Unable to delete the desired number of inventory items. Deleted " + deletedItems + " items.", "");
            return false;
        }
    }

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

    @FXML
    public void tryToAddActor() {
        System.out.println("Trying to add actor...");
        // Get the selected actor from the ComboBox
        Actor selectedActor = actorsCB.getValue();

        // Check if an actor is selected and not already in the ListView
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

}
