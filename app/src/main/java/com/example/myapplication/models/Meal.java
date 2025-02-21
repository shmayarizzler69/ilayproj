package com.example.myapplication.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Meal  implements Serializable {

    private int cal;
    private ArrayList<String> food;
    private String detail;
    private String id;

    public Meal() {
        this.food = new ArrayList<>();
    }

    public Meal(int cal, ArrayList<String> food, String detail, String id) {
        this.cal = cal;
        this.food = food;
        this.detail = detail;
        this.id = id;
    }

    public ArrayList<String> getFood() { return food; }
    public void setFood(ArrayList<String> food) { this.food = food; }

    public int getCal() { return cal; }
    public void setCal(int cal) { this.cal = cal; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getMealId() {
        return id;
    }

    public void setMealId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "cal=" + cal +
                ", food=" + food +
                ", detail='" + detail + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

