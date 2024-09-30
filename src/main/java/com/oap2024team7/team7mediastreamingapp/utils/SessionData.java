package com.oap2024team7.team7mediastreamingapp.utils;

import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Film;

/**
 * Class for the SessionData object.
 * This class is responsible for managing the session data of the application.
 * It stores the logged in customer, their address and selected film.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class SessionData {
    private static SessionData instance;
    private Customer loggedInCustomer;
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
        customerAddress = null;
        selectedFilm = null;
    }
}
