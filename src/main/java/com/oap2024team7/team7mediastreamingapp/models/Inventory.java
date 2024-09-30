package com.oap2024team7.team7mediastreamingapp.models;

/**
 * Inventory class
 * Represents an inventory object
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class Inventory {
    private int inventoryId;
    private int filmId;
    private int storeId;

    public Inventory(int inventoryId, int filmId, int storeId) {
        this.inventoryId = inventoryId;
        this.filmId = filmId;
        this.storeId = storeId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public int getFilmId() {
        return filmId;
    }

    public int getStoreId() {
        return storeId;
    }
}