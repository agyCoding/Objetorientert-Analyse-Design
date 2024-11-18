package com.oap2024team7.team7mediastreamingapp.utils;

import java.util.ArrayList;
import java.util.List;

import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.models.Staff;
import com.oap2024team7.team7mediastreamingapp.services.ProfileImageManager;

/**
 * Class for the SessionData object.
 * This class is responsible for managing the session data of the application.
 * It stores the logged in customer, current profile, customer's address, and selected film.
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
    private ProfileImageManager profileImageManager; // ProfileImageManager for database operations

    // Constructor is private to implement the Singleton pattern
    private SessionData() {
        savedFilms = new ArrayList<>(); // Initialize the saved films list
        profileImageManager = new ProfileImageManager(); // Initialize ProfileImageManager
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
    }

    public void clearProfileData() {
        currentProfile = null;
    }
    
    /**
     * Get the list of saved films.
     * This returns a copy of the saved films to ensure external modifications do not affect the session directly.
     * @return List of saved films.
     */
    public List<Film> getSavedFilms() {
        return new ArrayList<>(savedFilms); // Return a copy of the saved films list to prevent external modification
    }

    /**
     * Set the list of saved films.
     * Creates a copy of the provided list to ensure no external changes affect the internal state.
     * @param savedFilms List of films to set as the saved films.
     */
    public void setSavedFilms(List<Film> savedFilms) {
        if (savedFilms != null) {
            this.savedFilms = new ArrayList<>(savedFilms); // Create a copy to avoid external changes affecting the session
        } else {
            this.savedFilms = new ArrayList<>();
        }
    }

    /**
     * Add a film to the saved list.
     * @param film The film to add.
     */
    public void addFilmToSavedList(Film film) {
        if (film != null && !savedFilms.contains(film)) {
            savedFilms.add(film);
        }
    }

    /**
     * Remove a film from the saved list.
     * @param film The film to remove.
     */
    public void removeFilmFromSavedList(Film film) {
        if (film != null) {
            savedFilms.remove(film);
        }
    }

    /**
     * Save a profile image to the database.
     * @param profileId The ID of the profile.
     * @param imageData The image data as a byte array.
     * @return True if the image was successfully saved, false otherwise.
     */
    public boolean saveProfileImage(int profileId, byte[] imageData) {
        return profileImageManager.storeProfileImage(profileId, imageData);
    }

    /**
     * Retrieve a profile image from the database.
     * @param profileId The ID of the profile.
     * @return The image data as a byte array, or null if not found.
     */
    public byte[] getProfileImage(int profileId) {
        return profileImageManager.retrieveProfileImage(profileId);
    }
}
