package com.example.locationsystem.Entity;

public class Location {
    private String beaconName;
    private String location;
    private double x;
    private double y;

    public Location(String beaconName, String location, double x, double y) {
        this.beaconName = beaconName;
        this.location = location;
        this.x = x;
        this.y = y;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public void setBeaconName(String beaconName) {
        this.beaconName = beaconName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
