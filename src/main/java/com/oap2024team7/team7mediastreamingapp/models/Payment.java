package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDateTime;

/**
 * Represents a payment in the database.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class Payment {
    private int paymentId;
    private int customerId;
    private int staffId;
    private int rentalId;
    private double amount;
    private LocalDateTime paymentDate;

    // Complete constructor from the database
    public Payment(int paymentId, int customerId, int staffId, int rentalId, double amount, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.staffId = staffId;
        this.rentalId = rentalId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    // Constructor for creating a new payment, without paymentId, rentalId and payment date
    // This constructor is used when creating a new payment in the RentFilmController
    // The rentalId and payment date is set in the CreditCardPaymentController
    public Payment(int customerId, int staffId, double amount) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.amount = amount;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
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

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}
