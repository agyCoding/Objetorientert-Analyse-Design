package com.oap2024team7.team7mediastreamingapp.models;

public class Address {
    private int address_id; // automatically assigned in the database
    private String address;
    private String address2;
    private String district;
    private int city_id;
    private String postal_code;
    private String phone;
    private byte[] location; // GEOMETRY in the database
}
