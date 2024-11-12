package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.services.DatabaseManager;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        // Load films from the session data initially
        loadFilmsFromSession();

        // Refresh table to ensure data is displayed correctly
        myListTable.refresh();
    }

    // Load films from the session data
    private void loadFilmsFromSession() {
        // Load the user's saved movies from SessionData
        List<Film> savedFilms = SessionData.getInstance().getSavedFilms();

        if (savedFilms == null) {
            System.out.println("Warning: Saved films list from session is null, initializing empty list...");
            savedFilms = new ArrayList<>();
        }

        // Print all saved films for debugging purposes
        System.out.println("Number of films loaded from session: " + savedFilms.size());
        savedFilms.forEach(film -> System.out.println("Loaded Film: " + film.getTitle()));

        // Load the films into an observable list and set the TableView
        filmList = FXCollections.observableArrayList(savedFilms);
        myListTable.setItems(filmList);
    }

    // Load films directly from the database
    private void loadFilmsFromDatabase() {
        String selectQuery = "SELECT f.film_id, f.title, f.release_year, f.rating, f.description " +
                             "FROM film f"; // Adjust this query as needed to match your specific requirement

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery);
             ResultSet rs = stmt.executeQuery()) {

            filmList = FXCollections.observableArrayList();

            while (rs.next()) {
                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    Film.Rating.valueOf(rs.getString("rating"))
                );
                filmList.add(film);
            }

            myListTable.setItems(filmList);
            System.out.println("Number of films loaded from database: " + filmList.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        try {
            // Load the film details FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmdetails.fxml"));
            Parent root = loader.load();

            // Get the FilmDetailsController and set the selected film
            FilmDetailsController filmDetailsController = loader.getController();
            filmDetailsController.setSelectedFilm(film);

            // Create a new stage for the film details window
            Stage detailsStage = new Stage();
            detailsStage.setTitle(film.getTitle() + " Details");
            detailsStage.setScene(new Scene(root));
            detailsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading film details view.");
        }
    }

    // Handle refreshing the list
    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing the My Saved Films list...");
        loadFilmsFromSession();
        myListTable.refresh(); // Refresh the TableView after reloading the data
    }
}
