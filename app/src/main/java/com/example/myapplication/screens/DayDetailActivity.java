package com.example.myapplication.screens;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.example.myapplication.models.Meal;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.Adapters.MealAdapter;

public class DayDetailActivity extends AppCompatActivity {
    private TextView tvDayDetail;
    private RecyclerView rvMeals;
    private DatabaseService databaseService;
    private Day day;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        tvDayDetail = findViewById(R.id.tvDayDetail);
        rvMeals = findViewById(R.id.rvMeals);
        databaseService = DatabaseService.getInstance();

        // Retrieve the Day object from the Intent
        day = (Day) getIntent().getSerializableExtra("day");

        if (day != null) {
            // Display day details
            tvDayDetail.setText("Date: " + day.getDate().toString() + "\nTotal Calories: " + day.getSumcal());

            // Set up the MealAdapter with delete button functionality
            mealAdapter = new MealAdapter(this, day.getMeals(), meal -> {
                // Remove the meal from the day's meal list
                day.getMeals().remove(meal);
                day.setSumcal(day.calculateTotalCalories());

                // Update the day in Firebase
                String userId = AuthenticationService.getInstance().getCurrentUser().getId(); // Retrieve user ID
                databaseService.updateDay(day, userId, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void result) {
                        Toast.makeText(DayDetailActivity.this, "Meal deleted successfully!", Toast.LENGTH_SHORT).show();
                        mealAdapter.notifyDataSetChanged();
                        tvDayDetail.setText("Date: " + day.getDate().toString() + "\nTotal Calories: " + day.getSumcal());
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        Toast.makeText(DayDetailActivity.this, "Failed to delete meal: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            rvMeals.setLayoutManager(new LinearLayoutManager(this));
            rvMeals.setAdapter(mealAdapter);
        } else {
            Toast.makeText(this, "Error loading day details.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no day data is provided
        }
    }
}