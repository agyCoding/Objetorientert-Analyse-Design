package com.oap2024team7.team7mediastreamingapp.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.oap2024team7.team7mediastreamingapp.models.Film;

/**
 * MyRentalsManager class
 * This class is responsible for managing the user's rented films.
 * @author Magnus Fossaas (@magnuuus)
 */

public class MyRentalsManager {

    private final ObservableList<Film> rentedFilms;

    public MyRentalsManager() {
        // Initialize with rented films. Ideally, this would connect to a database.
        rentedFilms = FXCollections.observableArrayList();
        // Add logic to load rented films from database or data source.
    }

    public ObservableList<Film> getRentedFilms() {
        return rentedFilms;
    }

    // Add more methods as needed, such as adding a new rental, removing a rental, etc.
}
