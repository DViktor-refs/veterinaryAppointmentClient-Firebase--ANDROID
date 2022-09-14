package com.example.firebaselogin.models;

public class OwnerModel {

    private String ownerEmail;
    private String ownerName;
    private String ownerLastName;

    public OwnerModel(String ownerEmail, String ownerName, String ownerLastName) {
        this.ownerEmail = ownerEmail;
        this.ownerName = ownerName;
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }
}
