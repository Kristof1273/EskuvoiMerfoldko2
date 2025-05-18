package com.example.eskuvoihelyszinlefoglaloapp;

public class Venue {
    private String name;
    private int imageResourceId;
    private String location; // például "Budapest, Andrássy út 1." vagy GPS koordináták

    public Venue(String name, int imageResourceId, String location) {
        this.name = name;
        this.imageResourceId = imageResourceId;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getLocation() {
        return location;
    }
}
