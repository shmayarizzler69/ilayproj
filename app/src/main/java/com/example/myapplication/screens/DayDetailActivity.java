package com.example.myapplication.screens;

import android.os.Bundle;
import android.widget.Button;
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
        Button btnDeleteDay = findViewById(R.id.btnDeleteDay);

        // Retrieve the Day object from the Intent
        day = (Day) getIntent().getSerializableExtra("day");

        if (day != null) {
            // Validate meals list
            if (day.getMeals() == null || day.getMeals().isEmpty()) {
                Toast.makeText(this, "No meals available for this day.", Toast.LENGTH_SHORT).show();
            }

            // Display day details
            tvDayDetail.setText("Date: " + day.getDate().toString() + "\nTotal Calories: " + day.getSumcal());

            // Set up the MealAdapter
            mealAdapter = new MealAdapter(this, day.getMeals(), meal -> {
                if (meal != null) {
                    day.getMeals().remove(meal);
                    day.setSumcal(day.calculateTotalCalories());

                    String userId = databaseService.getCurrentUserId();
                    if (userId != null) {
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
                    } else {
                        Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DayDetailActivity.this, "Meal is null. Cannot delete.", Toast.LENGTH_SHORT).show();
                }
            });

            rvMeals.setLayoutManager(new LinearLayoutManager(this));
            rvMeals.setAdapter(mealAdapter);

            // Set up the delete day button
            btnDeleteDay.setOnClickListener(view -> {
                String userId = databaseService.getCurrentUserId();
                if (userId != null) {
                    databaseService.deleteDay(day.getDayId(), new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void result) {
                            Toast.makeText(DayDetailActivity.this, "Day deleted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Navigate back to the previous screen
                        }

                        @Override
                        public void onFailed(Exception exception) {
                            Toast.makeText(DayDetailActivity.this, "Failed to delete day: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Error loading day details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}