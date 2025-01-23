package com.example.myapplication.screens;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class test2 extends AppCompatActivity {

    private final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private final String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snacks"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Add header row
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Meal Type / Day"));
        for (String day : days) {
            headerRow.addView(createTextView(day));
        }
        tableLayout.addView(headerRow);

        // Add rows for meal types
        for (String meal : mealTypes) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(meal));

            for (int i = 0; i < days.length; i++) {
                EditText editText = new EditText(this);
                editText.setHint("Write here");
                row.addView(editText);
            }
            tableLayout.addView(row);
        }
    }

    // Helper method to create a TextView
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setGravity(android.view.Gravity.CENTER);
        return textView;
    }
}