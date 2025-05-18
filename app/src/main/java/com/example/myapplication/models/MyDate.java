package com.example.myapplication.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class MyDate implements Serializable, Comparable<MyDate> {
    private int year;
    private int month;
    private int day;

    public MyDate() {
        // Default constructor required for Firebase
    }

    public MyDate(Date date) {
        if (date != null) {
            @SuppressWarnings("deprecation")
            int year = date.getYear() + 1900;
            @SuppressWarnings("deprecation")
            int month = date.getMonth() + 1;
            @SuppressWarnings("deprecation")
            int day = date.getDate();
            
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }

    public MyDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Date asDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, this.year);
        calendar.set(Calendar.MONTH, this.month - 1); // Calendar months are 0-based
        calendar.set(Calendar.DAY_OF_MONTH, this.day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", day, month, year);
    }

    @Override
    public int compareTo(MyDate other) {
        if (this.year != other.year) {
            return Integer.compare(this.year, other.year);
        }
        if (this.month != other.month) {
            return Integer.compare(this.month, other.month);
        }
        return Integer.compare(this.day, other.day);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyDate myDate = (MyDate) o;
        return year == myDate.year && month == myDate.month && day == myDate.day;
    }
}
