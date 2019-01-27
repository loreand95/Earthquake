package com.univaq.loreand.earthquake.model;


import java.time.LocalDateTime;


public class Earthquake {


    private String id;

    private double magnitude;

    private String regionName;

    private double lat;

    private double lon;


    private LocalDateTime time;

    public Earthquake(){}

    public Earthquake(String id){
        this.id=id;
    }

    public Earthquake(String id, double magnitude, String regionName, double lat, double lon, LocalDateTime time) {
        this.id=id;
        this.magnitude = magnitude;
        this.regionName = regionName;
        this.lat = lat;
        this.lon = lon;
        this.time=time;
    }

    //Get & Setter

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}

