package com.example.myapplication.models;

import java.util.ArrayList;
import java.util.Date;

public class Day {
    private String id;
    private Date date;
    private ArrayList<Meal> meals;
    private int sumcal;



    public Day() {
        // Default constructor required for Firebase
        this.meals = new ArrayList<>();
    }


    public Day(String id, Date date, ArrayList<Meal> meals, int sumcal) {
        this.id = id;
        this.date = date;
        this.meals = meals;
        this.sumcal = sumcal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    public int getSumcal() {
        return sumcal;
    }

    public void setSumcal(int sumcal) {
        this.sumcal = sumcal;
    }


    public void addMeal(Meal meal) {
        this.meals.add( meal);
        this.sumcal+=meal.getCal();

    }

    @Override
    public String toString() {
        return "Day{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", meals=" + meals +
                ", sumcal=" + sumcal +
                '}';
    }
}
