package com.example.myapplication.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Meal  implements Serializable {

    private int cal;
    private ArrayList<String> food;
    private String detail;
// Add this field to link meals to users




    public Meal() {
        this.food = new ArrayList<>();
    }

    public Meal(int cal, ArrayList<String> food, String detail) {
        this.cal = cal;
        this.food = food;
        this.detail = detail;
    }

    public ArrayList<String> getFood() { return food; }
    public void setFood(ArrayList<String> food) { this.food = food; }

    public int getCal() { return cal; }
    public void setCal(int cal) { this.cal = cal; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }


    @Override
    public String toString() {
        return "Meal{" +
                "cal=" + cal +
                ", food=" + food +
                ", detail='" + detail + '\'' +
                '}';
    }
}
