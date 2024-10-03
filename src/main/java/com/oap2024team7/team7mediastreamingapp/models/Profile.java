package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDate;

public class Profile {
    private int profileId;
    private int customerId;
    private boolean isMainProfile;
    private String profileName;
    private LocalDate birthDate;

    // Constructor for creating a new profile from the database (with profileId)
    public Profile(int profileId, int customerId, boolean isMainProfile, String profileName, LocalDate birthDate) {
        this.profileId = profileId;
        this.customerId = customerId;
        this.isMainProfile = isMainProfile;
        this.profileName = profileName;
        this.birthDate = birthDate;
    }

    // Constructor for creating a new profile in the application (without profileId, that will get automatically assigned in the database)
    public Profile(int customerId, String profileName, LocalDate birthDate) {
        this.customerId = customerId;
        this.profileName = profileName;
        this.isMainProfile = false; // By default, a new profile is not the main profile
        this.birthDate = birthDate;
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
}
