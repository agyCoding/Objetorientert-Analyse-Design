package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDate;

/**
 * Class for the Profile object.
 * This class represents a profile of a customer in the application.
 * It stores the profile ID, customer ID, profile name, birth date and hashed password.
 * It also stores a boolean value to indicate if the profile is the main profile of the customer.
 * The profile ID is automatically assigned by the database when a new profile is created.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class Profile {
    private int profileId;
    private int customerId;
    private boolean isMainProfile;
    private String profileName;
    private LocalDate birthDate;
    private String hashedPassword;

    // Constructor for creating a new profile from the database (with profileId)
    public Profile(int profileId, int customerId, boolean isMainProfile, String profileName, LocalDate birthDate, String hashedPassword) {
        this.profileId = profileId;
        this.customerId = customerId;
        this.isMainProfile = isMainProfile;
        this.profileName = profileName;
        this.birthDate = birthDate;
        this.hashedPassword = hashedPassword;
    }

    // Constructor for creating a new profile in the application (without profileId, that will get automatically assigned in the database)
    public Profile(int customerId, String profileName, LocalDate birthDate) {
        this.customerId = customerId;
        this.profileName = profileName;
        this.isMainProfile = false; // By default, a new profile is not the main profile
        this.birthDate = birthDate;
        this.hashedPassword = null; // By default, a new profile does not have a password
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public boolean isMainProfile() {
        return isMainProfile;
    }

    public void setIsMainProfile(boolean isMainProfile) {
        this.isMainProfile = isMainProfile;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
