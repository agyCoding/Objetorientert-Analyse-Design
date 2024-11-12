package com.oap2024team7.team7mediastreamingapp.models;

/**
 * Class for the Staff object.
 * This class is responsible for creating and managing Staff objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class Staff {
    private int staffId; // automatically assigned in the database
    private String firstName;
    private String lastName;
    private int addressId;
    private String email;
    private int storeId; // NN in the database so we will hardcode it to 1 in case of staff creation
    @SuppressWarnings("unused")
    private int active; // this maps to TINYINT in the database with 1 for active and 0 for inactive
    private String username;
    private String password; // hashed password
    
    // Constructor for creating a new staff from the database
    public Staff(int staffId, String firstName, String lastName, int addressId, String email, int storeId, int active, String username, String password) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addressId = addressId;
        this.email = email;
        this.storeId = storeId;
        this.active = active;
        this.username = username;
        this.password = password;
    }

    // Constructor for creating a new staff to be added to the database
    public Staff(String firstName, String lastName, int addressId, String email, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.addressId = addressId;
        this.email = email;
        this.storeId = 1; // Hardcoded to 1
        this.active = 1; // Hardcoded to 1
        this.username = username;
        this.password = password;
    }

    // We don't need all getters and setters since 1. We're not setting staffId in the application and 2. We're not planning on managing staff's actvity or store connection

    public int getstaffId() {
        return staffId;
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

    public int getaddressId() {
        return addressId;
    }

    public void setaddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
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
