package com.oap2024team7.team7mediastreamingapp.models;

public class Staff {
    int staff_id; // automatically assigned in the database
    String first_name;
    String last_name;
    int address_id;
    String email;
    int store_id; // we are not planning on using this attribute but it's NN in the database so we will hardcode it to 1 in case of staff creation
    int active; // this maps to TINYINT in the database with 1 for active and 0 for inactive
    String username;
    String password; // hashed password
    
    // Constructor for creating a new staff from the database
    public Staff(int staff_id, String first_name, String last_name, int address_id, String email, int store_id, int active, String username, String password) {
        this.staff_id = staff_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.address_id = address_id;
        this.email = email;
        this.store_id = store_id;
        this.active = active;
        this.username = username;
        this.password = password;
    }

    // Constructor for creating a new staff to be added to the database
    public Staff(String first_name, String last_name, int address_id, String email, String username, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.address_id = address_id; // Have to add handling of the address_id based on "regular" address information
        this.email = email;
        this.store_id = 1; // Hardcoded to 1
        this.active = 1; // Hardcoded to 1
        this.username = username;
        this.password = password;
    }

    // We don't need all getters and setters since 1. We're not setting staff_id in the application and 2. We're not planning on managing staff's actvity or store connection

    public int getStaff_id() {
        return staff_id;
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

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
