package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
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
        // Initialize the columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        releaseYearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Load the user's saved movies
        List<Film> savedFilms = SessionData.getInstance().getSavedFilms();
        filmList = FXCollections.observableArrayList(savedFilms);
        myListTable.setItems(filmList);
    }

    // Handle removing a film from the list
    @FXML
    private void handleRemoveFromList() {
        Film selectedFilm = myListTable.getSelectionModel().getSelectedItem();
        if (selectedFilm != null) {
            SessionData.getInstance().removeFilmFromSavedList(selectedFilm);
            filmList.remove(selectedFilm); // Update the TableView
        }
    }

    // Handle sorting by title
    @FXML
    private void handleSortByTitle() {
        filmList.sort(Comparator.comparing(Film::getTitle));
    }

    // Handle sorting by release year
    @FXML
    private void handleSortByYear() {
        filmList.sort(Comparator.comparing(Film::getReleaseYear));
    }

    // Handle viewing film details
    @FXML
    private void handleViewDetails() {
        Film selectedFilm = myListTable.getSelectionModel().getSelectedItem();
        if (selectedFilm != null) {
            showFilmDetails(selectedFilm);
        }
    }

    // Show the details of the selected film
    private void showFilmDetails(Film film) {
        try {
            // Load the film details FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer/contentmanagement/filmdetails.fxml"));
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
            // Optionally, show an alert if there's an error loading the details
        }
    }
}
