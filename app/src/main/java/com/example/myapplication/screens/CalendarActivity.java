package com.example.myapplication.screens;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private TextView monthYearText;
    private GridLayout calendarGrid;
    private ArrayList<Day> userDays;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        monthYearText = findViewById(R.id.monthYearText);
        calendarGrid = findViewById(R.id.calendarGrid);

        databaseReference = FirebaseDatabase.getInstance().getReference("Days"); // Reference to Firebase "Days"

        userDays = new ArrayList<>();

        // Fetch the days where meals were added (this could be any month, for example)
        fetchDaysWithMeals();

        // Set up the month and year label
        monthYearText.setText("January 2025"); // You can update this dynamically
    }

    private void fetchDaysWithMeals() {
        // Query Firebase for the days with meals
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDays.clear();
                for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                    Day day = daySnapshot.getValue(Day.class);
                    userDays.add(day);
                }

                // After fetching data, populate the calendar grid
                populateCalendarGrid();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CalendarActivity.this, "Failed to load days.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateCalendarGrid() {
        // Clear previous grid layout
        calendarGrid.removeAllViews();

        // Loop through the userDays and create a button for each day
        for (final Day day : userDays) {
            Button dayButton = new Button(this);
          /*  dayButton.setText(day.getDayInWeek()); // Display the day number */
            dayButton.setLayoutParams(new GridLayout.LayoutParams());
            dayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* openMealPage(day.getDayInWeek()); // Open MealPage activity for this day */
                }
            });

            // Add the button to the grid layout
            calendarGrid.addView(dayButton);
        }
    }

    private void openMealPage(String selectedDate) {
        // Pass the selected date to MealPage activity
        Intent intent = new Intent(CalendarActivity.this, MealPage.class);
        intent.putExtra("selectedDate", selectedDate);
        startActivity(intent);
    }
}