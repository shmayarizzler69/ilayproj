package com.example.myapplication.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Day  implements Serializable {
    private String id;
    private MyDate date;
    private ArrayList<Meal> meals;
    private int sumcal;



    public Day() {
        // Default constructor required for Firebase
        this.meals = new ArrayList<>();
    }


    public Day(String id, MyDate date, ArrayList<Meal> meals, int sumcal) {
        this.id = id;
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

    public MyDate getDate() {
        return date;
    }

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
                total += meal.getCal();  // Assuming each Meal has a `getCalories()` method
            }
        }
        return total;
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


