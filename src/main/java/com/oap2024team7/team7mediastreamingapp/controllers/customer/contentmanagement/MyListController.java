package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.util.Comparator;

/**
 * This class is responsible for handling the My List page.
 * It displays the user's saved films, allowing them to add, remove, view and sort films from their personal My List.
 * @author Adnan Duric (@adovic)
 */

public class MyListController {

    @FXML
    private TableView<Film> myListTable;

    @FXML
    private TableColumn<Film, String> titleColumn;

    @FXML
    private TableColumn<Film, Integer> releaseYearColumn;

    @FXML
    private TableColumn<Film, Film.Rating> ratingColumn;

    private ObservableList<Film> filmList;

    @FXML
    public void initialize() {
        System.out.println("Initializing MyListController...");

        // Initialize the columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        releaseYearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        loadFilmsFromDatabase();

        // Refresh table to ensure data is displayed correctly
        myListTable.refresh();
    }

    // Load films directly from the database
    private void loadFilmsFromDatabase() {
        Profile currentProfile = SessionData.getInstance().getCurrentProfile();
        if (currentProfile == null) {
            System.out.println("No current profile found.");
            return;
        }

        int profileId = currentProfile.getProfileId();
        filmList = FXCollections.observableArrayList(FilmManager.getFilmsFromMyList(profileId));

        myListTable.setItems(filmList);
        System.out.println("Number of films loaded from database: " + filmList.size());
    }

    // Handle removing a film from the list
    @FXML
    private void handleRemoveFromList() {
        Film selectedFilm = myListTable.getSelectionModel().getSelectedItem();
        if (selectedFilm != null) {
            Profile currentProfile = SessionData.getInstance().getCurrentProfile();
            if (currentProfile != null) {
                System.out.println("Removing film: " + selectedFilm.getTitle() + " for profile ID: " + currentProfile.getProfileId());
                FilmManager.removeFilmFromMyList(currentProfile.getProfileId(), selectedFilm.getFilmId());
                SessionData.getInstance().removeFilmFromSavedList(selectedFilm);
                filmList.remove(selectedFilm); // Update the TableView
                myListTable.refresh(); // Refresh table after removal
            }
        } else {
            System.out.println("No film selected for removal.");
        }
    }

    // Handle adding a film to the list (Assuming there's a way to select a film to add)
    @FXML
    private void handleAddToList(Film film) {
        Profile currentProfile = SessionData.getInstance().getCurrentProfile();
        if (currentProfile != null && !filmList.contains(film)) {
            System.out.println("Adding film: " + film.getTitle() + " to profile ID: " + currentProfile.getProfileId());
            FilmManager.addFilmToMyList(currentProfile.getProfileId(), film.getFilmId());
            SessionData.getInstance().addFilmToSavedList(film);
            filmList.add(film); // Update the TableView
            myListTable.refresh(); // Refresh table after adding a film
        } else {
            System.out.println("Film is already in the list or no profile found.");
        }
    }

    // Handle sorting by title
    @FXML
    private void handleSortByTitle() {
        System.out.println("Sorting films by title...");
        filmList.sort((f1, f2) -> f1.getTitle().compareToIgnoreCase(f2.getTitle()));
        myListTable.refresh();
    }
    
    

    // Handle sorting by release year
    @FXML
    private void handleSortByYear() {
        System.out.println("Sorting films by release year...");
        filmList.sort(Comparator.comparing(Film::getReleaseYear));
        myListTable.refresh(); // Refresh table to reflect sorted data
    }

    // Handle viewing film details
    @FXML
    private void handleViewDetails() {
        Film selectedFilm = myListTable.getSelectionModel().getSelectedItem();
        if (selectedFilm != null) {
            System.out.println("Viewing details for film: " + selectedFilm.getTitle());
            showFilmDetails(selectedFilm);
        } else {
            System.out.println("No film selected for viewing details.");
        }
    }

    // Show the details of the selected film
    private void showFilmDetails(Film film) {
        SessionData.getInstance().setSelectedFilm(film);

        StageUtils.showPopup(
            (Stage) myListTable.getScene().getWindow(),
            "filmDetails",  // Using the short name for the FXML file
            "Streamify - Film Details",
            Modality.WINDOW_MODAL  // Specify the modality
        );
    }

    // Handle refreshing the list
    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing the My Saved Films list...");
        loadFilmsFromDatabase();
        myListTable.refresh(); // Refresh the TableView after reloading the data
    }
}
