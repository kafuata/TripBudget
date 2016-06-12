package com.example.clarisselawson.tripbudget;

import java.util.Date;

/**
 * Created by clarisselawson on 11/06/16.
 */
public class Spent {
    public static final String[] CATEGORIES = {
            "Alimentation", "Shopping", "Transport", "Divertissement"
    };

    private int id;
    private String libelle;
    private float amount;
    private int category;
    private Date date;
    private Trip trip;

    public Spent(int id, String libelle, float amount, int category, Date date, Trip trip) {
        this.id = id;
        this.libelle = libelle;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.trip = trip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
