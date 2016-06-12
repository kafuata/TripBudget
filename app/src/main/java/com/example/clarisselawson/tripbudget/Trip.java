package com.example.clarisselawson.tripbudget;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by clarisselawson on 11/06/16.
 */
public class Trip implements Parcelable {

    private int id;
    private String destination;
    private Float budget;
    private Date startDate;
    private Date finishDate;

    public Trip(int id, String destination, Float budget, Date startDate, Date finishDate) {
        this.id = id;
        this.budget = budget;
        this.destination = destination;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    // Parcelable interface methods ===========================
    // from: http://www.parcelabler.com/

    protected Trip(Parcel in) {
        id = in.readInt();
        destination = in.readString();
        budget = in.readByte() == 0x00 ? null : in.readFloat();
        long tmpStartDate = in.readLong();
        startDate = tmpStartDate != -1 ? new Date(tmpStartDate) : null;
        long tmpFinishDate = in.readLong();
        finishDate = tmpFinishDate != -1 ? new Date(tmpFinishDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(destination);
        if (budget == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(budget);
        }
        dest.writeLong(startDate != null ? startDate.getTime() : -1L);
        dest.writeLong(finishDate != null ? finishDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}
