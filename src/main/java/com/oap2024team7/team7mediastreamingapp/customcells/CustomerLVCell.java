package com.oap2024team7.team7mediastreamingapp.customcells;

import com.oap2024team7.team7mediastreamingapp.models.Customer;

import javafx.scene.control.ListCell;

/**
 * Custom ListCell for displaying Customer objects with first name, last name and email only
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class CustomerLVCell extends ListCell<Customer> {
    @Override
    protected void updateItem(Customer customer, boolean empty) {
        super.updateItem(customer, empty);
        if (empty || customer == null) {
            setText(null);
        } else {
            // Display the first name, last name and email in the ListView
            setText(customer.getFirstName() + " " + customer.getLastName() + " (" + customer.getEmail() + ")");
        }
    }
    
}
