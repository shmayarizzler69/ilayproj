package com.example.myapplication.models;

import java.util.ArrayList;

public class Meal {

    protected String id;
    protected int cal;
    protected ArrayList <String> food;

    protected String detail;
    public Meal() {
        food = new ArrayList<>();
    }
    public Meal(String id, ArrayList<String> food, int cal, String detail) {
        this.id = id;
        this.food = food;
        this.cal = cal;
        this.detail = detail;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getFood() {
        return food;
    }

    public void setFood(ArrayList<String> food) {
        this.food = food;
    }
    public void addFood(String item) {
        food.add(item);
    }

    public int getCal() {
        return cal;
    }

    public void setCal(int cal) {
        this.cal = cal;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    @Override
    public String toString() {
        return "Meal{" +
                "id='" + id + '\'' +
                ", cal=" + cal +
                ", food=" + food +
                ", detail='" + detail + '\'' +
                '}';
    }



}
