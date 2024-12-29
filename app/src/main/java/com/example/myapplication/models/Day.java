package com.example.myapplication.models;

import java.util.ArrayList;

public class Day {
    protected String id;
    protected String day;
    protected ArrayList<Meal> food= new ArrayList<>();
    protected int sumcal;

    public Day(String id, String day, ArrayList<Meal> food, int sumcal) {
        this.id = id;
        this.day = day;
        this.food = food;
        this.sumcal = sumcal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<Meal> getFood() {
        return food;
    }

    public void setFood(ArrayList<Meal> food) {
        this.food = food;
    }

    public int getSumcal() {
        return sumcal;
    }

    public void setSumcal(Integer sumcal) {
        this.sumcal = sumcal;
    }

    @Override
    public String toString() {
        return "Day{" +
                "id='" + id + '\'' +
                ", day='" + day + '\'' +
                ", food=" + food +
                ", sumcal=" + sumcal +
                '}';
    }
}
