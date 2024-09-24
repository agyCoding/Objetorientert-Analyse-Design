package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.models.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBWriter;

public class AddressManager {
    private static AddressManager instance;

    public static AddressManager getInstance() {
        if (instance == null) {
            instance = new AddressManager();
        }
        return instance;
    }

    public int getCityIdFromCityName(String cityName) {
        try {
             // First, normalize the cityName string, ensuring that's one capital letter, and the rest is regular letter and that there aren't multiple empty spaces in the string
            String normalizedCityName = GeneralUtils.normalizeString(cityName);

            // Next, query the table "city" in the database to find city_id for the cityName sent into the method
            String query = "SELECT city_id FROM city WHERE city = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, normalizedCityName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("City ID for " + normalizedCityName + " is " + rs.getInt(1));
                    int cityId = rs.getInt(1);
                    return cityId;  // Return the city_id for the cityName
                } else {
                    // If the cityName is not found in the table, insert new city into the table. The insert should put cityName to the "city" field in the table and hardcore 103 for country_id
                    String insertQuery = "INSERT INTO city (city, country_id) VALUES (?, 103)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, normalizedCityName);
                        insertStmt.executeUpdate();
                        System.out.println("City " + normalizedCityName + " was not found in the database. It was added to the database.");
                        return getCityIdFromCityName(normalizedCityName);  // Recursively call the method to get the city_id for the cityName that was just added to the table
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return -1;  // Return -1 if there was an error in the database query
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;  // Return -1 if there was an error in the database query
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 if there was an error in the method
        }
    }
    
    public String getCityNameFromCityId(int cityId) {
        try {
            // Query the table "city" in the database to find city for the city_id sent into the method
            String query = "SELECT city FROM city WHERE city_id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cityId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("City for city_id " + cityId + " is " + rs.getString(1));
                    String cityName = rs.getString(1);
                    return cityName;  // Return the city for the city_id
                } else {
                    System.out.println("City for city_id " + cityId + " was not found in the database.");
                    return null;  // Return null if city not found
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;  // Return null if there was an error in the database query
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if there was an error in the method
        }
    }

    // Create address object from the database with the address_id
    public static Address getAddressById(int addressId) {
        String query = "SELECT address, district, city_id, postal_code, phone, location FROM address WHERE address_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setInt(1, addressId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String address = rs.getString("address");
                String district = rs.getString("district");
                int cityId = rs.getInt("city_id");
                String postalCode = rs.getString("postal_code");
                String phone = rs.getString("phone");
                byte[] locationWKB = rs.getBytes("location");  // Fetch GEOMETRY as WKB (byte[])
    
                // Create and return the Address object using the constructor that accepts WKB
                return new Address(addressId, address, district, cityId, postalCode, phone, locationWKB);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return null;  // Return null if address not found or if there's an error
    }

    // This method will check if the address already exists and if not register a new address
    // The method also returns the id of the address
    public static int registerAddress(Address newAddress) {
        try {
            // Query the table "address" in the database to find address_id for the address, district, cityId, postalCode, and phone sent into the method (to check if it already exists)
            String query = "SELECT address_id FROM address WHERE address = ? AND district = ? AND city_id = ? AND postal_code = ? AND phone = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newAddress.getAddress());
                stmt.setString(2, newAddress.getDistrict());
                stmt.setInt(3, newAddress.getCityId());
                stmt.setString(4, newAddress.getPostalCode());
                stmt.setString(5, newAddress.getPhone());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Address already exists in the database. Address ID is " + rs.getInt(1));
                    return rs.getInt(1);  // Return the address_id for the address, district, cityId, postalCode, and phone
                } else {
                    // If the combination of address, district, cityId, postalCode and phone doesn't exist, create new address
                    String insertQuery = "INSERT INTO address (address, district, city_id, postal_code, phone, location) VALUES (?, ?, ?, ?, ?, ST_GeomFromWKB(?))";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, newAddress.getAddress());
                        insertStmt.setString(2, newAddress.getDistrict());
                        insertStmt.setInt(3, newAddress.getCityId());
                        insertStmt.setString(4, newAddress.getPostalCode());
                        insertStmt.setString(5, newAddress.getPhone());

                        // Convert from Point to byte[] using JTS, before sending to DB
                        if (newAddress.getLocation() != null) {
                            WKBWriter wkbWriter = new WKBWriter();
                            byte[] locationWKB = wkbWriter.write(newAddress.getLocation());
                            insertStmt.setBytes(6, locationWKB); // Store the geometry as WKB (byte[])
                        } else {
                            // Handle the case where the location is null
                            throw new IllegalArgumentException("Invalid location for address");
                        }

                        insertStmt.executeUpdate();
                        System.out.println("Address was not found in the database. It was added to the database.");
                        return registerAddress(newAddress);  // Recursively call the method to get the address_id for the address that was just added to the table
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return -1;  // Return -1 if there was an error in the database query
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;  // Return -1 if there was an error in the database query
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 if there was an error in the method
        }
    }
}
