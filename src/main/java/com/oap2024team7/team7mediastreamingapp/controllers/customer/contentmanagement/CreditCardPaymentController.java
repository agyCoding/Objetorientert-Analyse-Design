package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDateTime;

import com.itextpdf.text.Document;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Rental;
import com.oap2024team7.team7mediastreamingapp.services.PaymentManager;
import com.oap2024team7.team7mediastreamingapp.services.RentalManager;
import com.oap2024team7.team7mediastreamingapp.models.Payment;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javafx.stage.FileChooser;
import java.io.File;

/**
 * Controller for the credit card payment view.
 * Handles payment processing and generating receipts.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class CreditCardPaymentController {
    @FXML
    private Label filmTitleLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private TextField cardNumberTF;
    @FXML
    private TextField monthTextField;
    @FXML
    private TextField yearTF;
    @FXML
    private TextField cvvTF;

    private double amount;
    private Payment newPayment;
    private RentalManager rentalManager;
    private PaymentManager paymentManager;

    @FXML
    private void initialize() {
        System.out.println("Initializing CreditCardPaymentController");
        Film selectedFilm = SessionData.getInstance().getSelectedFilm();
        newPayment = SessionData.getInstance().getNewPayment();
        rentalManager = new RentalManager();
        paymentManager = new PaymentManager();

        amount = newPayment.getAmount();

        filmTitleLabel.setText("Film title: " + selectedFilm.getTitle());
        amountLabel.setText("Amount: " + amount);

        // Add listeners to text fields to restrict input
        cardNumberTF.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d{0,16}")) {
                cardNumberTF.setText(oldText); // Restrict to digits and max length of 16
            }
        });

        monthTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d{0,2}")) {
                monthTextField.setText(oldText); // Restrict to digits and max length of 2
            }
        });

        yearTF.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d{0,4}")) {
                yearTF.setText(oldText); // Restrict to digits and max length of 4
            }
        });

        cvvTF.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d{0,3}")) {
                cvvTF.setText(oldText); // Restrict to digits and max length of 3
            }
        });
    }

    @FXML
    private void tryToPay() {
        System.out.println("Trying to pay");
        processPayment();
    }

    /**
     * Processes the payment by adding the rental and payment to the database.
     */
    private void processPayment() {
        Rental newRental = SessionData.getInstance().getNewRental(); 
        newPayment.setPaymentDate(LocalDateTime.now());

        Stage stage = (Stage) filmTitleLabel.getScene().getWindow();

        // Try adding rental to the database
        int rentalId = rentalManager.addRentalToDatabase(newRental);

        if (rentalId < 0) {
            // Handle rental failure
            Platform.runLater(() -> {
                GeneralUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while trying to rent the film.", "Please try again.");
            });
            return;
        } else {
            // Set the rental ID in the payment object
            newRental.setRentalId(rentalId);
            newPayment.setRentalId(rentalId);

            // Try adding payment to the database
            int paymentId = paymentManager.addPaymentToDatabase(newPayment);
            if (paymentId < 0) {
                // Handle payment failure
                Platform.runLater(() -> {
                    GeneralUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while processing the payment.", "Please try again.");
                });

                // Rollback rental if payment fails
                rentalManager.removeRentalFromDatabase(rentalId);
                return;
            } else {
                // Set the payment ID in newPayment object
                newPayment.setPaymentId(paymentId);

                generateReceipt(SessionData.getInstance().getSelectedFilm(), newRental, newPayment, stage);

                // If both rental and payment are successful, show success message
                Platform.runLater(() -> {
                    GeneralUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Payment successful!", "Your rental has been processed.");
                });
                stage.close();
            }
        }
    }

    /**
     * Generates a PDF receipt for the rental and payment.
     * @param selectedFilm
     * @param newRental
     * @param newPayment
     * @param stage
     */
    private void generateReceipt(Film selectedFilm, Rental newRental, Payment newPayment, Stage stage) {
         // Open file chooser to select directory
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        // Open the save file dialog, but only show directories
        fileChooser.getExtensionFilters().clear(); // Remove all filters first
        fileChooser.setInitialFileName("receipt_" + newRental.getRentalId() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        
        // Get the directory path from the user
        File selectedFile = fileChooser.showSaveDialog(stage);

        // If a file was chosen, proceed with creating and saving the PDF
        if (selectedFile != null) {
            try {
                // Get the path for the selected directory
                String filePath = selectedFile.getAbsolutePath();
                if (!filePath.endsWith(".pdf")) {
                    filePath += ".pdf"; // Ensure the file ends with .pdf
                }

                // Initialize PDF document
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Title and general details
                document.add(new Paragraph("Rental Receipt"));
                document.add(new Paragraph("-----------------------------------------------------"));

                // Film Information
                document.add(new Paragraph("Film Title: " + selectedFilm.getTitle()));
                document.add(new Paragraph("Rental ID: " + newRental.getRentalId()));
                document.add(new Paragraph("Payment ID: " + newPayment.getPaymentId()));

                // Rental Dates and Payment Information
                document.add(new Paragraph("Rental Start Date: " + newRental.getRentalDate()));
                document.add(new Paragraph("Rental End Date: " + newRental.getReturnDate()));
                document.add(new Paragraph("Rental Rate per Day: " + selectedFilm.getRentalRate()));
                document.add(new Paragraph("Amount Paid: " + newPayment.getAmount()));
                document.add(new Paragraph("Payment Date: " + newPayment.getPaymentDate()));

                // Add some space before the footer
                document.add(new Paragraph("-----------------------------------------------------"));

                // Footer information
                document.add(new Paragraph("Thank you for your rental!"));
                document.add(new Paragraph("For any inquiries, please contact customer support."));

                // Close the document
                document.close();

                System.out.println("Receipt generated at: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No file selected.");
        }
    }
    
    
}
