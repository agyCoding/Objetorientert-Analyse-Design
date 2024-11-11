package com.oap2024team7.team7mediastreamingapp.utils;

import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Staff;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the SessionData object.
 * This class is responsible for managing the session data of the application.
 * It stores the logged in customer, current profile, customer's address and selected film.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class SessionData {
    private static SessionData instance;
    private Customer loggedInCustomer;
    private Staff loggedInStaff;
    private Profile currentProfile;
    private Address customerAddress;
    private Film selectedFilm;
    private List<Film> savedFilms; // List to store saved films
    private List<Film> rentedFilms; // List to store rented films

    // Constructor is private to implement the Singleton pattern
    private SessionData() {
        savedFilms = new ArrayList<>(); // Initialize the saved films list
        rentedFilms = new ArrayList<>(); // Initialize the rented films list
    }

    public static SessionData getInstance() {
        if (instance == null) {
            instance = new SessionData();
        }
        return instance;
    }

    public Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    public void setLoggedInCustomer(Customer loggedInCustomer) {
        this.loggedInCustomer = loggedInCustomer;
    }

    public Staff getLoggedInStaff() {
        return loggedInStaff;
    }

    public void setLoggedInStaff(Staff loggedInStaff) {
        this.loggedInStaff = loggedInStaff;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(Profile currentProfile) {
        this.currentProfile = currentProfile;
    }

    public Address getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(Address customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setSelectedFilm(Film selectedFilm) {
        this.selectedFilm = selectedFilm;
    }

    public Film getSelectedFilm() {
        return selectedFilm;
    }

    public void clearSessionData() {
        loggedInCustomer = null;
        loggedInStaff = null;
        currentProfile = null;
        customerAddress = null;
        selectedFilm = null;
        savedFilms.clear(); // Clear saved films when session is cleared
        rentedFilms.clear(); // Clear rented films when session is cleared
    }

    public void clearProfileData() {
        currentProfile = null;
    }
    
    /**
     * Get the list of saved films
     * @return List of saved films
     */
    public List<Film> getSavedFilms() {
        return savedFilms;
    }

    /**
     * Add a film to the saved list
     * @param film The film to add
     */
    public void addFilmToSavedList(Film film) {
        if (!savedFilms.contains(film)) {
            savedFilms.add(film);
        }
    }

    /**
     * Remove a film from the saved list
     * @param film The film to remove
     */
    public void removeFilmFromSavedList(Film film) {
        savedFilms.remove(film);
    }

    /**
     * Get the list of rented films
     * @return List of rented films
     */
    public List<Film> getRentedFilms() {
        return rentedFilms;
    }

    /**
     * Add a film to the rented list
     * @param film The film to add
     */
    public void addFilmToRentedList(Film film) {
        if (!rentedFilms.contains(film)) {
            rentedFilms.add(film);
        }
    }

    /**
     * Remove a film from the rented list
     * @param film The film to remove
     */
    public void removeFilmFromRentedList(Film film) {
        rentedFilms.remove(film);
    }
}
