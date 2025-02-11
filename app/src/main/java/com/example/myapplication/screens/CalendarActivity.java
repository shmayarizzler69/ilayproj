package com.example.myapplication.screens;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.example.myapplication.models.Meal;
import com.example.myapplication.models.MyDate;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Adapters.CalendarAdapter;

public class CalendarActivity extends AppCompatActivity {
    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private ArrayList<Day> days;
    private int daysInMonth;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);

        // Initialize calendar
        calendar = Calendar.getInstance();
        loadMonth(calendar);

        // Set up RecyclerView
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7)); // 7 columns
    }

    private void loadMonth(Calendar calendar) {
        days = new ArrayList<>();
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Fill `days` with dummy data or fetch from database
        for (int i = 0; i < daysInMonth; i++) {
            Day day = new Day();
            day.setSumcal((int) (Math.random() * 500)); // Dummy calorie data
            days.add(day);
        }

        calendarAdapter = new CalendarAdapter(this, days, daysInMonth);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
}