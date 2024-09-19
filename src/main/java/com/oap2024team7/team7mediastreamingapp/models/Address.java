package com.oap2024team7.team7mediastreamingapp.models;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;

public class Address {
    private int addressId; // automatically assigned in the database
    private String address;
    private String district;
    private int cityId;
    private String postalCode;
    private String phone;
    private Point location; // GEOMETRY in the database, NN.

    // Constructor for loading an address from the database, including the GEOMETRY (WKB format)
    public Address(String address, String district, int cityId, String postalCode, String phone, byte[] locationWKB) throws ParseException {
        this.address = address;
        this.district = district;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;

        // Convert WKB to Point using JTS
        WKBReader reader = new WKBReader();
        this.location = (Point) reader.read(locationWKB);  // Convert WKB to Point
    }

    // Constructor for creating a new address to be added to the database
    public Address(String address, String district, int cityId, String postalCode, String phone) {
        this.address = address;
        this.district = district;
        this.cityId = cityId; // Have to add handling of the city_id based on "regular" city information
        this.postalCode = postalCode;
        this.phone = phone;
        // Hardcoded default GEOMETRY (Point 0, 0 in WKB format)
        this.location = new byte[]{
            0x00, 0x00, 0x00, 0x01,  // Byte order and type (Point)
            0x00, 0x00, 0x00, 0x01,  // Geometry type (Point)
            0x00, 0x00, 0x00, 0x00,  // X-coordinate (0.0 as double)
            0x00, 0x00, 0x00, 0x00,  // Y-coordinate (0.0 as double)
        };
    }

    public int getAddressId() {
        return addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getLocation() {
        return location;
    }

    public void setLocation(byte[] location) {
        this.location = location;
    }
}
