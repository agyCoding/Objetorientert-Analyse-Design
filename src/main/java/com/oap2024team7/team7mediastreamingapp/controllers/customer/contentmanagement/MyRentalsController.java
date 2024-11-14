package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.MyRentalsManager;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.util.Comparator;
import java.util.List;

/**
 * MyRentalsController class
 * This class is responsible for handling the user's rented films view
 * It displays the user's rented films, but is currently limited to only saving SessionData, 
 * not connecting to a database.
 * @author Magnus Fossaas (@magnuuus)
 */

public class MyRentalsController {

    @FXML
    private TableView<Film> myRentalsTable;

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

        // Load the user's rented movies from the database
        MyRentalsManager rentalsManager = new MyRentalsManager();
        List<Film> rentedFilms = rentalsManager.loadRentedFilmsFromDatabase();
        filmList = FXCollections.observableArrayList(rentedFilms);
        myRentalsTable.setItems(filmList);
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
        Film selectedFilm = myRentalsTable.getSelectionModel().getSelectedItem();
        if (selectedFilm != null) {
            showFilmDetails(selectedFilm);
        }
    }

    // Show the details of the selected film
    private void showFilmDetails(Film film) {
        SessionData.getInstance().setSelectedFilm(film);

        StageUtils.showPopup(
            (Stage) myRentalsTable.getScene().getWindow(),
            "filmDetails",  // Using the short name for the FXML file
            "Streamify - Film Details",
            Modality.WINDOW_MODAL  // Specify the modality
        );
    }
}
