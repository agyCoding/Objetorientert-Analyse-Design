package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDateTime;

/**
 * Represents a rental in the database.
 * A rental is a record of a customer renting a film from the store.
 * @author Agata (Agy) Olaussen @agyCoding
 */
public class Rental {
    private int rentalId;
    private int inventoryId;
    private int customerId;
    private int staffId;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;

    // Complete constructor for creating rental from the database
    public Rental(int rentalId, int inventoryId, int customerId, int staffId, LocalDateTime rentalDate, LocalDateTime returnDate) {
        this.rentalId = rentalId;
        this.inventoryId = inventoryId;
        this.customerId = customerId;
        this.staffId = staffId;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }

    // Constructor for creating a new rental, without rentalId
    public Rental(int inventoryId, int customerId, int staffId, LocalDateTime rentalDate, LocalDateTime returnDate) {
        this.inventoryId = inventoryId;
        this.customerId = customerId;
        this.staffId = staffId;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public LocalDateTime getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDateTime rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
}
