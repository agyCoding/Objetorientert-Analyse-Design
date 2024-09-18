package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDate;

// This is our basic user class (that will be able to view content)
// This is called Customer to match the database schema
public class Customer {
    private int customer_id; // automatically assigned in the database
    private String first_name;
    private String last_name;
    private String email;
    private int address_id;
    private int active; // this maps to TINYINT in the database with 1 for active and 0 for inactive
    private LocalDate create_date;

    // Constructor for creating a new customer from the database
    public Customer(int customer_id, String first_name, String last_name, String email, int address_id, int active, LocalDate create_date) {
        this.customer_id = customer_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.address_id = address_id;
        this.active = active;
        this.create_date = create_date;
    }

    // Constructor for creating a new customer to be added to the database
    public Customer(String first_name, String last_name, String email, int address_id, int active) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.address_id = address_id; // Have to add handling of the address_id based on "regular" address information
        this.active = active;
        this.create_date = LocalDate.now(); // Automatically set to the current date when the object is created
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public String getFirst_name() {
        return first_name;
        }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

}
