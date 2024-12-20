package com.oap2024team7.team7mediastreamingapp.models;

/**
 * Class for the Actor object.
 * This class is responsible for creating and managing Actor objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class Actor {
    private int actorId;
    private String firstName;
    private String lastName;

    // Constructor for creating a new actor from the database
    public Actor(int actorId, String firstName, String lastName) {
        this.actorId = actorId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getActorId() {
        return actorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
