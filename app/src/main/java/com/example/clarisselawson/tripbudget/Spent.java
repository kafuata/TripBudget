package com.example.clarisselawson.tripbudget;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by clarisselawson on 11/06/16.
 */
public class Spent implements Parcelable {
    public static final String[] CATEGORIES = {
            "Alimentation", "Shopping", "Transport", "Divertissement"
    };

    private long id;
    private String libelle;
    private float amount;
    private int category;
    private Date date;
    private Trip trip;


    private long tripId;

    public Spent(long id, String libelle, float amount, int category, Date date, Trip trip) {
        this.id = id;
        this.libelle = libelle;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.trip = trip;
        if (trip != null) {
            this.tripId = trip.getId();
        }
    }

    public Spent(long id, String libelle, float amount, int category, Date date) {
        this(id, libelle, amount, category, date, null);
    }

    public Spent(long id, String libelle, float amount, int category, Date date, int tripId) {
        this(id, libelle, amount, category, date, null);
        this.tripId = tripId;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    // Parcelable interface methods ===========================
    // from: http://www.parcelabler.com/

    protected Spent(Parcel in) {
        id = in.readLong();
        libelle = in.readString();
        amount = in.readFloat();
        category = in.readInt();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        trip = (Trip) in.readValue(Trip.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(libelle);
        dest.writeFloat(amount);
        dest.writeInt(category);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeValue(trip);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Spent> CREATOR = new Parcelable.Creator<Spent>() {
        @Override
        public Spent createFromParcel(Parcel in) {
            return new Spent(in);
        }

        @Override
        public Spent[] newArray(int size) {
            return new Spent[size];
        }
    };
}
