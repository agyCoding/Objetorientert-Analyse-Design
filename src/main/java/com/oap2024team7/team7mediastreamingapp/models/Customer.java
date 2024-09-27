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
    private LocalDate birthDate; // LOCAL ATTRIBUTE just for application
    private LocalDate createDate;
    private AccountType accountType; // LOCAL ATTRIBUTE just for application

    public enum AccountType {
        FREE, PREMIUM
    }

    // Constructor for creating a new customer from the database
    public Customer(int customerId, String firstName, String lastName, String email, int addressId, int active, LocalDate createDate) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressId = addressId;
        this.active = active;
        this.birthDate = LocalDate.of(2000, 1, 1); // For all users coming from the database (that doesn't have information about birthdate, set birthdate to 1.1.2000)
        this.createDate = createDate;
        this.accountType = AccountType.FREE; // For all users coming from the database (that doesn't have information about account type, set account type to FREE)
    }

    // Constructor for creating a new customer in the application
    public Customer(String firstName, String lastName, String email, int addressId, int active, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressId = addressId; // Have to add handling of the addressId based on "regular" address information
        this.active = active;
        this.birthDate = birthDate;
        this.createDate = LocalDate.now(); // Automatically set to the current date when the object is created
        this.accountType = AccountType.PREMIUM; // For all users created in the application, set account type to FREE
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
