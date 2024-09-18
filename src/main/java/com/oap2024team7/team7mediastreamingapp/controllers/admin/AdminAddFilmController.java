// Last Modified: 11.11.2024
package com.oap2024team7.team7mediastreamingapp.controllers.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.oap2024team7.team7mediastreamingapp.customcells.ActorComboBoxCell;
import com.oap2024team7.team7mediastreamingapp.customcells.AdminActorCell;
import com.oap2024team7.team7mediastreamingapp.customcells.AdminSpecialFeaturesCell;
import com.oap2024team7.team7mediastreamingapp.customcells.CategoryCell;
import com.oap2024team7.team7mediastreamingapp.customcells.LanguageCell;
import com.oap2024team7.team7mediastreamingapp.customcells.RatingCell;
import com.oap2024team7.team7mediastreamingapp.models.Actor;
import com.oap2024team7.team7mediastreamingapp.models.Category;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Language;
import com.oap2024team7.team7mediastreamingapp.services.ActorManager;
import com.oap2024team7.team7mediastreamingapp.services.CategoryManager;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.services.InventoryManager;
import com.oap2024team7.team7mediastreamingapp.services.LanguageManager;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for the AdminAddFilm.fxml file.
 * This class is responsible for handling the the user interactions and updating the UI for the page that allows the admin to add a new film to the database.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class AdminAddFilmController {
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

    // Local variables
    private Film newFilm = new Film();
    private Stage stage;
    private List<Actor> selectedActors = new ArrayList<>();
    private List<String> selectedSpecialFeatures = new ArrayList<>();
    private CategoryManager categoryManager = new CategoryManager();
    private InventoryManager inventoryManager = new InventoryManager();
    private FilmManager filmManager = new FilmManager();
        
    // Store information about chosen inputs
    private Category selectedCategory;
    private String selectedSpecialFeature;
    private Language selectedLanguage = null;

    /**
     * Pre-load values for the ComboBoxes
     */
    public void initialize() {     
        loadCategories();
        loadRatings();
        loadLanguages();
        loadSpecialFeatures();
        loadActors();
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

        // Define custom cell for displaying special features in the List View
        specialFeaturesLV.setCellFactory(lv -> new AdminSpecialFeaturesCell(this));
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

        // Define custom cell for displaying actors in the List View
        actorsLV.setCellFactory(lv -> new AdminActorCell(this));
    }

    /**
     * Method for adding a new film to the database.
     * The method checks if all required fields are filled and if the information in the fields is valid.
     * If the information is valid, the method adds the film to the database and the inventory.
     */
    @FXML
    private void tryToAddFilm() {
        System.out.println("Trying to add film");
        // Get the values from the input fields related to the Film class
        String title = filmTitleTF.getText();
        String description = descriptionTA.getText();
        String releaseYear = releaseYearTF.getText();
        Film.Rating rating = pgRatingCB.getValue();
        Language language = languageCB.getValue();

        Set<String> specialFeatures = new HashSet<>(specialFeaturesLV.getItems());
        List<Actor> actors = actorsLV.getItems();

        Category category = categoryCB.getValue();

        // Check if all required fields are filled
        if (title.isEmpty() || language == null || category == null) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Missing information", "Please fill in all required fields.");
            return;
        }

        // Validate information in fields that require a specific format
        if (!GeneralUtils.isNumeric(releaseYear) || !GeneralUtils.isNumeric(rentalDurationTF.getText()) || !GeneralUtils.isNumeric(lengthTF.getText()) || !GeneralUtils.isNumeric(rentalRateTF.getText())) {
            GeneralUtils.showAlert(AlertType.WARNING, "Warning", "Invalid information", "Please enter valid information in the fields.");
            return;
        }

        int releaseYearInt = Integer.parseInt(releaseYear);
        int rentalDuration = Integer.parseInt(rentalDurationTF.getText());
        int length = Integer.parseInt(lengthTF.getText());
        double rentalRate = Double.parseDouble(rentalRateTF.getText());
        
        // Add information about the film to the new film object
        newFilm.setTitle(title);
        newFilm.setDescription(description);
        newFilm.setReleaseYear(releaseYearInt);
        newFilm.setLanguage(language);
        newFilm.setRentalDuration(rentalDuration);
        newFilm.setLength(length);
        newFilm.setRating(rating);
        newFilm.setSpecialFeatures(specialFeatures);
        newFilm.setRentalRate(rentalRate);

        newFilm.setActors(actors);

        int newFilmId = filmManager.addFilm(newFilm);
        int storeId = SessionData.getInstance().getLoggedInStaff().getStoreId();

        // Add the film to the database
        if (newFilmId > 0) {
            // Add the film to the inventory
            int inventoryAmount = Integer.parseInt(inventoryAmountTF.getText());
            if (inventoryManager.addInventoryForFilm(newFilmId, storeId, inventoryAmount)) {
                GeneralUtils.showAlert(AlertType.INFORMATION, "Success", "Film added", "The film has been successfully added to the inventory.");
                // Close the window
                stage = (Stage) filmTitleTF.getScene().getWindow();
                stage.close();
            } else {
                GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to add film", "An error occurred while trying to add the film to the inventory.");
            }
        } else {
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to add film", "An error occurred while trying to add the film");
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
            specialFeaturesLV.getItems().remove(specialFeature);
        }
        selectedSpecialFeatures.clear();
        updateDeleteSpecialFeaturesButtonVisibility();
    }
}
