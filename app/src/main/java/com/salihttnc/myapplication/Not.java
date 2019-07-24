package com.salihttnc.myapplication;

public class Not {

    private String note,tittle,date,clock,lat,lng,radius;


    public Not() {
    }

    public Not(String note, String tittle, String date, String clock, String lat, String lng, String radius) {
        this.note = note;
        this.tittle = tittle;
        this.date = date;
        this.clock = clock;
        this.lat = lat;
        this.lng = lng;
        this.radius=radius;

    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
