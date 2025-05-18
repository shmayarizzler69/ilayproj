package com.example.myapplication.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Day implements Serializable {
    private String id;
    private String title;
    private String description;
    private String userId;
    private MyDate date;
    private ArrayList<Meal> meals;
    private int sumcal;

    public Day() {
        // Default constructor required for Firebase
        this.meals = new ArrayList<>();
    }

    public Day(String id, String title, String description, String userId, MyDate date, ArrayList<Meal> meals, int sumcal) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.date = date;
        this.meals = meals;
        this.sumcal = sumcal;
    }

    public String getDayId() {
        return id;
    }

    public void setDayId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MyDate getDate() {
        return date;
    }

    // Single setDate method that handles both Date and MyDate
    public void setDate(MyDate date) {
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

    public int calculateTotalCalories() {
        int total = 0;
        if (meals != null) {
            for (Meal meal : meals) {
                total += meal.getCal();
            }
        }
        return total;
    }

    public void addMeal(Meal meal) {
        this.meals.add(meal);
        this.sumcal += meal.getCal();
    }

    @Override
    public String toString() {
        return "Day{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", meals=" + meals +
                ", sumcal=" + sumcal +
                '}';
    }
}


