package com.example.myapplication.models;

import java.util.ArrayList;

public class Week {
    String id;
    String date;
    ArrayList<Day> days;
    public Week(String id, String date, ArrayList<Day> days) {
        this.id = id;
        this.date = date;
        this.days = days;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public void setDays(ArrayList<Day> days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "Week{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", days=" + days +
                '}';
    }
}
