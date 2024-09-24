package com.oap2024team7.team7mediastreamingapp.utils;

import com.oap2024team7.team7mediastreamingapp.models.Address;
import com.oap2024team7.team7mediastreamingapp.models.Customer;

public class SessionData {
    private static SessionData instance;
    private Customer loggedInCustomer;
    private Address customerAddress;

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
}
