package com.example.myapplication.screens;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.models.Meal;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Addfood extends AppCompatActivity {
    private LinearLayout container;
    private Meal meal;
    private TextView foodListTextView;
    private HashMap<Integer, String> foodInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood); // Ensure your layout file name matches here

        meal = new Meal(); // Initialize Meal instance
        container = findViewById(R.id.container);
        foodListTextView = findViewById(R.id.foodListTextView);
        foodInputs = new HashMap<>();

        // Add Row Button
        Button addRowButton = findViewById(R.id.addRowButton);
        addRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRowWithTwoEditTexts();
            }
        });

        // Remove Row Button
        Button removeRowButton = findViewById(R.id.removeRowButton);
        removeRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastRow();
            }
        });

        // Print ArrayList Button
        Button printListButton = findViewById(R.id.printListButton);
        printListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printFoodList();
            }
        });

        // Set up Spinner for meal type
        Spinner mealTypeSpinner = findViewById(R.id.mealTypeSpinner);

        // Create a list of meal types
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack", "Other"};

        // Set up an ArrayAdapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mealTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);

        // Handle spinner selection
        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected meal type
                String selectedMealType = mealTypes[position];

                // Set the detail field in the Meal object
                meal.setDetail(selectedMealType);

                // Optional: Show a toast message
                Toast.makeText(Addfood.this, "Selected meal type: " + selectedMealType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection case if needed
            }
        });
    }

    // Adds a new row with two EditTexts
    private void addNewRowWithTwoEditTexts() {
        LinearLayout newRow = new LinearLayout(this);
        newRow.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, 8, 0, 8);
        newRow.setLayoutParams(rowParams);

        // Create the first EditText (only numbers)
        EditText firstEditText = new EditText(this);
        firstEditText.setHint("Enter number");
        firstEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout.LayoutParams firstEditTextParams = new LinearLayout.LayoutParams(
                0, // Weight-based width
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // Split the row evenly
        );
        firstEditTextParams.setMargins(8, 0, 8, 0);
        firstEditText.setLayoutParams(firstEditTextParams);

        // Create the second EditText (only letters)
        EditText secondEditText = new EditText(this);
        secondEditText.setHint("Enter food");
        secondEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout.LayoutParams secondEditTextParams = new LinearLayout.LayoutParams(
                0, // Weight-based width
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // Split the row evenly
        );
        secondEditTextParams.setMargins(8, 0, 8, 0);
        secondEditText.setLayoutParams(secondEditTextParams);

        // Add a TextWatcher to update Meal in real-time
        secondEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int rowIndex = container.indexOfChild(newRow);
                String input = s.toString().trim();

                if (!input.isEmpty()) {
                    foodInputs.put(rowIndex, input);
                } else {
                    foodInputs.remove(rowIndex);
                }

                meal.setFood(new ArrayList<>(foodInputs.values()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Add the EditTexts to the new row
        newRow.addView(firstEditText);
        newRow.addView(secondEditText);

        // Add the row to the container
        container.addView(newRow);
    }

    // Removes the last row from the container
    private void removeLastRow() {
        int rowCount = container.getChildCount();
        if (rowCount > 0) {
            int lastRowIndex = rowCount - 1;
            container.removeViewAt(lastRowIndex);
            foodInputs.remove(lastRowIndex);

            // Update Meal.food list
            meal.setFood(new ArrayList<>(foodInputs.values()));

            Toast.makeText(this, "Last row removed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No rows to remove.", Toast.LENGTH_SHORT).show();
        }
    }

    // Prints the contents of the Meal.food ArrayList
    private void printFoodList() {
        StringBuilder foodList = new StringBuilder("Food List:\n");

        for (int i = 0; i < container.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) container.getChildAt(i);

            // Ensure the row has at least two EditTexts
            if (row.getChildCount() >= 2) {
                EditText firstEditText = (EditText) row.getChildAt(0); // Calories (first EditText)
                EditText secondEditText = (EditText) row.getChildAt(1); // Food (second EditText)

                String calories = firstEditText.getText().toString().trim();
                String foodItem = secondEditText.getText().toString().trim();

                if (!calories.isEmpty() && !foodItem.isEmpty()) {
                    foodList.append("Calories: ").append(calories)
                            .append(", Food: ").append(foodItem)
                            .append("\n");
                }
            }
        }

        foodListTextView.setText(foodList.toString());
    }
}