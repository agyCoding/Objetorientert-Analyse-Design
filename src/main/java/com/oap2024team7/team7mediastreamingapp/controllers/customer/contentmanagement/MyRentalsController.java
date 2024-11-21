package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.MyRentalsManager;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * MyRentalsController class
 * This class is responsible for handling the user's rented films view.
 * Combines playback functionality, film details, and database integration.
 * @author Magnus Bjordammen (@magnuuus)
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

    @FXML
    private Button playFilmButton;

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

        // Disable play button by default
        playFilmButton.setDisable(true);

        // Enable play button only when a film is selected
        myRentalsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            playFilmButton.setDisable(newSelection == null);
        });
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

    // Handle the play button action
    @FXML
    public void playFilm() {
        try {
            // Load the playback FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/customer/contentmanagement/playback.fxml"));
            Parent root = fxmlLoader.load();

            // Create a new stage for the playback screen
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true); // Open in fullscreen mode
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        // Save the selected film to SessionData
        SessionData.getInstance().setSelectedFilm(film);

        // Open a pop-up for film details
        StageUtils.showPopup(
            (Stage) myRentalsTable.getScene().getWindow(),
            "filmDetails",  // Using the short name for the FXML file
            "Streamify - Film Details",
            Modality.WINDOW_MODAL  // Specify the modality
        );
    }
}