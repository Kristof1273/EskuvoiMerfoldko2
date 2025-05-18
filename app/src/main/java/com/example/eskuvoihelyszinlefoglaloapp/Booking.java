package com.example.eskuvoihelyszinlefoglaloapp;

public class Booking {
    private String venueName;
    private String date;
    private String status;

    public Booking(String venueName, String date, String status) {
        this.venueName = venueName;
        this.date = date;
        this.status = status;
    }

    public String getVenueName() { return venueName; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}
