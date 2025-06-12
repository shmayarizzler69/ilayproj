package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class DayDetailActivity extends AppCompatActivity {
    private TextView tvDayDetail;
    private TextView tvCalories;
    private TextView tvEmptyState;
    private TextView tvTitle;
    private TextView tvDescription;
    private RecyclerView rvMeals;
    private DatabaseService databaseService;
    private Day day;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        tvDayDetail = findViewById(R.id.tvDayDetail);
        tvCalories = findViewById(R.id.tvCalories);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        rvMeals = findViewById(R.id.rvMeals);
        databaseService = DatabaseService.getInstance();
        Button btnDeleteDay = findViewById(R.id.btnDeleteDay);
        ExtendedFloatingActionButton fabAddMeal = findViewById(R.id.fabAddMeal);

        // Retrieve the Day object from the Intent
        day = (Day) getIntent().getSerializableExtra("day");

        if (day != null) {
            // Update UI based on meals availability
            updateMealsUI();

            // Display day details
            tvDayDetail.setText(day.getDate().toString());
            tvCalories.setText(String.valueOf(day.getSumcal()));
            tvTitle.setText(day.getTitle());
            tvDescription.setText(day.getDescription());

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
                                updateMealsUI();
                                tvDayDetail.setText(day.getDate().toString());
                                tvCalories.setText(String.valueOf(day.getSumcal()));
                                tvTitle.setText(day.getTitle());
                                tvDescription.setText(day.getDescription());
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

            // Set up the add meal button
            fabAddMeal.setOnClickListener(v -> {
                Intent addMealIntent = new Intent(this, AddFood.class);
                addMealIntent.putExtra("day", day);
                startActivity(addMealIntent);
            });

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

    private void updateMealsUI() {
        if (day.getMeals() == null || day.getMeals().isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvMeals.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvMeals.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the day data when returning from AddFood activity
        if (day != null) {
            String userId = databaseService.getCurrentUserId();
            if (userId != null) {
                databaseService.getDayById(userId, day.getDayId(), new DatabaseService.DatabaseCallback<Day>() {
                    @Override
                    public void onCompleted(Day updatedDay) {
                        if (updatedDay != null) {
                            day = updatedDay;
                            updateMealsUI();
                            if (day.getMeals() != null) {
                                mealAdapter = new MealAdapter(DayDetailActivity.this, day.getMeals(), meal -> {
                                    if (meal != null) {
                                        day.getMeals().remove(meal);
                                        day.setSumcal(day.calculateTotalCalories());
                                        databaseService.updateDay(day, userId, new DatabaseService.DatabaseCallback<Void>() {
                                            @Override
                                            public void onCompleted(Void result) {
                                                Toast.makeText(DayDetailActivity.this, "Meal deleted successfully!", Toast.LENGTH_SHORT).show();
                                                mealAdapter.notifyDataSetChanged();
                                                updateMealsUI();
                                                tvDayDetail.setText(day.getDate().toString());
                                                tvCalories.setText(String.valueOf(day.getSumcal()));
                                                tvTitle.setText(day.getTitle());
                                                tvDescription.setText(day.getDescription());
                                            }

                                            @Override
                                            public void onFailed(Exception exception) {
                                                Toast.makeText(DayDetailActivity.this, "Failed to delete meal: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                rvMeals.setAdapter(mealAdapter);
                                tvDayDetail.setText(day.getDate().toString());
                                tvCalories.setText(String.valueOf(day.getSumcal()));
                                tvTitle.setText(day.getTitle());
                                tvDescription.setText(day.getDescription());
                            }
                        }
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        Toast.makeText(DayDetailActivity.this, "Failed to refresh day data: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}