package com.example.myapplication.screens;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.example.myapplication.models.Meal;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;

public class DayDetailActivity extends AppCompatActivity {
    private TextView tvDayDetail, tvMeals;
    private DatabaseService databaseService;
    Day day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        tvDayDetail = findViewById(R.id.tvDayDetail);
        tvMeals = findViewById(R.id.tvMeals);
        databaseService = DatabaseService.getInstance();

        day = (Day) getIntent().getSerializableExtra("day");

        if (day != null) {


            tvDayDetail.setText("Date: " + day.getDate().toString() +
                            "\nTotal Calories: " + day.getSumcal());

                    StringBuilder mealsDetails = new StringBuilder();
                    for (Meal meal : day.getMeals()) {
                        mealsDetails.append(meal.getDetail()).append(":\n");
                        for (String food : meal.getFood()) {
                            mealsDetails.append("- ").append(food).append("\n");
                        }
                        mealsDetails.append("\n");
                    }

                    tvMeals.setText(mealsDetails.toString());






        }
    }
}