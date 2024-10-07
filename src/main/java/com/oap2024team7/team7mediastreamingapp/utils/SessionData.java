package com.oap2024team7.team7mediastreamingapp.utils;

import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Staff;

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

    private SessionData() {}

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
    }

    public void clearProfileData() {
        currentProfile = null;
    }
}
