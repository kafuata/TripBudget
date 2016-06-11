package com.example.clarisselawson.tripbudget;

import java.util.Date;

/**
 * Created by clarisselawson on 11/06/16.
 */
public class Trip {

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

}
