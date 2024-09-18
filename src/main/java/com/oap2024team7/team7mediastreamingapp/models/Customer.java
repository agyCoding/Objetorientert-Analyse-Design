package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDate;

// This is our basic user class (that will be able to view content)
// This is called Customer to match the database schema
public class Customer {
    private int customerId; // automatically assigned in the database
    private String firstName;
    private String lastName;
    private String email;
    private int addressId;
    private int active; // this maps to TINYINT in the database with 1 for active and 0 for inactive
    private LocalDate createDate;

    // Constructor for creating a new customer from the database
    public Customer(int customerId, String firstName, String lastName, String email, int addressId, int active, LocalDate createDate) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressId = addressId;
        this.active = active;
        this.createDate = createDate;
    }

    // Constructor for creating a new customer to be added to the database
    public Customer(String firstName, String lastName, String email, int addressId, int active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressId = addressId; // Have to add handling of the addressId based on "regular" address information
        this.active = active;
        this.createDate = LocalDate.now(); // Automatically set to the current date when the object is created
    }

    public int getcustomerId() {
        return customerId;
    }

    public String getfirstName() {
        return firstName;
        }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getlastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getaddressId() {
        return addressId;
    }

    public void setaddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

}
