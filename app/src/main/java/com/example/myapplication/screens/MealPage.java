package com.example.myapplication.screens;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.example.myapplication.models.Meal;
import java.util.ArrayList;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MealPage extends AppCompatActivity {
    private TextView dayLabel;
    private ListView mealsListView;
    private ArrayList<Meal> mealsList;
    private String selectedDate;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_page);

        dayLabel = findViewById(R.id.dayLabel);
        mealsListView = findViewById(R.id.mealsListView);

        // Retrieve selected date passed from CalendarActivity
        selectedDate = getIntent().getStringExtra("selectedDate");

        // Set the label to show the selected date
        dayLabel.setText("Meals for: " + selectedDate);

        // Simulate retrieving the meals for that day

        // Create an ArrayAdapter to display meals in the ListView
        ArrayAdapter<Meal> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mealsList);
        mealsListView.setAdapter(adapter);
    }

    // Sample method to simulate getting meals for the selected day


    // If you want to fetch from Firebase, replace the getMealsForDay() method with a Firebase query
    private void getMealsForDayFromFirebase(String date) {
        // Example Firebase query to get meals for a specific day
        DatabaseReference mealsRef = FirebaseDatabase.getInstance().getReference("Meals");
        mealsRef.orderByChild("day").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mealsList.clear();
                for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    mealsList.add(meal);
                }

                // Update the ListView with the fetched meals
                ArrayAdapter<Meal> adapter = new ArrayAdapter<>(MealPage.this, android.R.layout.simple_list_item_1, mealsList);
                mealsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MealPage.this, "Failed to load meals.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}