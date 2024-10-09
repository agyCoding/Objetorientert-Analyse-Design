package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import java.util.List;
import java.util.HashSet;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private ComboBox<String> actorsCB;
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
    private CategoryManager categoryManager = new CategoryManager();
        

    // Store information about chosen inputs
    private Category selectedCategory;
    private String selectedSpecialFeature;
    private Language selectedLanguage = null;



    public void initialize() {     
        loadDummyValues();
        loadCategories();
        loadRatings();
        loadLanguages();
        loadSpecialFeatures();
        loadCurrentSpecialFeatures();
        loadCurrentActors();       
    }

    private void loadDummyValues() {
        // Create an ObservableList of dummy actors
        ObservableList<String> dummyActors = FXCollections.observableArrayList(
            "Actor 1", 
            "Actor 2", 
            "Actor 3", 
            "Actor 4"
        );
        
        // Set the items to the ComboBox
        actorsCB.setItems(dummyActors);
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
        selectedFilm = film;
        
        // Now that the film is set, update the labels with the film's details
        updateFilmDetails();

        // Save the selected film to the session data
        SessionData.getInstance().setSelectedFilm(selectedFilm);
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
        int currentInventorySize = currentInventory.size();
        
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

        Film film = SessionData.getInstance().getSelectedFilm();
        if (film != null && film.getSpecialFeatures() != null) {
            // Add the special features of the selected film to the ListView
            specialFeaturesLV.getItems().addAll(film.getSpecialFeatures());
        }
    }

    private void loadCurrentActors() {
        // Clear existing items
        actorsLV.getItems().clear();

        // Set custom cells for displaying actors
        actorsLV.setCellFactory(lv -> new AdminActorCell(this));

        Film film = SessionData.getInstance().getSelectedFilm();
        if (film != null && film.getActors() != null) {
            // Add the actors of the selected film to the ListView
            actorsLV.getItems().addAll(film.getActors());
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
        selectedFilm.setReleaseYear(Integer.parseInt(releaseYear));
        selectedFilm.setRating(rating);
        selectedFilm.setLanguage(language);
        selectedFilm.setSpecialFeatures(new HashSet<>(specialFeatures));
        selectedFilm.setActors(actors);



        // Try to update the film in the database
        // boolean updated = selectedFilm.updateFilm(selectedFilm);

        // Try to update category in the database
        // boolean updated = categoryManager.updateCategoryForFilm(selectedFilm, category);
    }
    @FXML
    public void tryToAddSF() {
        System.out.println("Trying to add special feature...");
            // Get the selected special feature from the ComboBox
            selectedSpecialFeature = specialfeaturesCB.getValue();
            
            // Check if a special feature is selected and not already in the ListView
            if (selectedSpecialFeature != null && !specialFeaturesLV.getItems().contains(selectedSpecialFeature)) {
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
    }

}
