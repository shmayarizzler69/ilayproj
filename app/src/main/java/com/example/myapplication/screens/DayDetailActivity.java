package com.example.myapplication.screens;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.example.myapplication.models.Meal;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;

public class DayDetailActivity extends AppCompatActivity {
    private TextView detailsTextView;

    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        detailsTextView = findViewById(R.id.detailsTextView);

        databaseService = DatabaseService.getInstance();

        String dayId = getIntent().getStringExtra("dayId");

        databaseService.getDayById(AuthenticationService.getInstance().getCurrentUserId(), dayId, new DatabaseService.DatabaseCallback<Day>() {
            @Override
            public void onCompleted(Day day) {
                if (day != null) {
                    StringBuilder details = new StringBuilder();
                    details.append("Date: ").append(day.getDate().toString()).append("\n");
                    details.append("Total Calories: ").append(day.getSumcal()).append("\n");
                    details.append("Meals:\n");

                    for (Meal meal : day.getMeals()) {
                        details.append("- ").append(meal.getDetail()).append(": ")
                                .append(meal.getCal()).append(" cal\n");
                    }

                    detailsTextView.setText(details.toString());
                }
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }
}