package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDate;

/**
 * Class for the Customer object.
 * This class is responsible for creating and managing Customer objects.
 * It also contains the birth date and account type information for the customer.
 * This is our basic user class (that will be able to view content).
 * This is called Customer to match the database schema
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class Customer {
    private int customerId; // automatically assigned in the database
    private String firstName;
    private String lastName;
    private String email;
    private int addressId;
    private int active; // this maps to TINYINT in the database with 1 for active and 0 for inactive
    private LocalDate createDate;
    private AccountType accountType; // this attribute is added to the database schema on application start
    private int storeId;

    public enum AccountType {
        FREE, PREMIUM
    }

    // Constructor for creating a new customer from the database
    public Customer(int customerId, String firstName, String lastName, String email, int addressId, int active, LocalDate createDate, AccountType accountType, int storeId) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressId = addressId;
        this.active = active;
        this.createDate = createDate;
        // If the account type is not set, set it to FREE
        if (accountType == null) {
            this.accountType = AccountType.FREE;
        } else {
            this.accountType = accountType;
        }
        this.storeId = storeId;
    }

    // Constructor for creating a new customer in the application
    public Customer(String firstName, String lastName, String email, int addressId, int active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressId = addressId; // Have to add handling of the addressId based on "regular" address information
        this.active = active;
        this.createDate = LocalDate.now(); // Automatically set to the current date when the object is created
        this.accountType = AccountType.FREE; // For all users created in the application, set account type to FREE
        this.storeId = 1; // For all users created in the application, set storeId to 1
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", addressId=" + addressId +
                ", active=" + active +
                ", createDate=" + createDate +
                ", accountType=" + accountType +
                ", storeId=" + storeId +
                '}';
    }
}
