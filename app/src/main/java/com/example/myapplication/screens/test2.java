package com.example.myapplication.screens;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.models.Meal;

public class test2 extends AppCompatActivity {

    private LinearLayout container;
    private Meal meal;
    private TextView foodListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        meal = new Meal(); // Initialize Meal instance
        container = findViewById(R.id.container);
        foodListTextView = findViewById(R.id.foodListTextView);

        // Add Row Button
        Button addRowButton = findViewById(R.id.addRowButton);
        addRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRowWithTwoEditTexts();
            }
        });

        // Submit All Second Parts Button
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllSecondPartsToMeal();
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

        // Sum Calories Button
        Button sumCaloriesButton = findViewById(R.id.sumCaloriesButton);
        sumCaloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumCalories();
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

        // Restrict input to numbers only
        firstEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        firstEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)}); // Optional: set a max length

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

        // Restrict input to letters only (using regex filter)
        secondEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30), new InputFilter.AllCaps()}); // Optional: Limit length and convert to uppercase
        secondEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout.LayoutParams secondEditTextParams = new LinearLayout.LayoutParams(
                0, // Weight-based width
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // Split the row evenly
        );
        secondEditTextParams.setMargins(8, 0, 8, 0);
        secondEditText.setLayoutParams(secondEditTextParams);

        // Add the EditTexts to the new row
        newRow.addView(firstEditText);
        newRow.addView(secondEditText);

        // Add the row to the container
        container.addView(newRow);
    }

    // Collects text from all second EditTexts and adds them to Meal
    private void addAllSecondPartsToMeal() {
        int rowCount = container.getChildCount();
        if (rowCount == 0) {
            Toast.makeText(this, "No rows to process.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAnyFieldAdded = false;

        for (int i = 0; i < rowCount; i++) {
            LinearLayout row = (LinearLayout) container.getChildAt(i);

            // Ensure the row has at least two EditTexts
            if (row.getChildCount() < 2) continue;

            // Get the second EditText
            EditText secondEditText = (EditText) row.getChildAt(1);

            // Get the text and add it to Meal if not empty
            String foodItem = secondEditText.getText().toString().trim();
            if (!foodItem.isEmpty()) {
                meal.addFood(foodItem);
                secondEditText.setText(""); // Optionally clear the field after adding
                isAnyFieldAdded = true;
            }
        }

        if (isAnyFieldAdded) {
            Toast.makeText(this, "All second parts have been added to the list.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No valid second parts found.", Toast.LENGTH_SHORT).show();
        }
    }

    // Removes the last row from the container
    private void removeLastRow() {
        int rowCount = container.getChildCount();
        if (rowCount > 0) {
            container.removeViewAt(rowCount - 1);
            Toast.makeText(this, "Last row removed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No rows to remove.", Toast.LENGTH_SHORT).show();
        }
    }

    // Prints the contents of the Meal.food ArrayList
    private void printFoodList() {
        if (meal.getFood().isEmpty()) {
            foodListTextView.setText("The food list is empty.");
        } else {
            StringBuilder foodList = new StringBuilder("Food List:\n");

            // Iterate through rows and print both calories and food
            for (int i = 0; i < container.getChildCount(); i++) {
                LinearLayout row = (LinearLayout) container.getChildAt(i);

                // Ensure the row has at least two EditTexts
                if (row.getChildCount() >= 2) {
                    EditText firstEditText = (EditText) row.getChildAt(0); // Calories (first EditText)
                    EditText secondEditText = (EditText) row.getChildAt(1); // Food (second EditText)

                    String calories = firstEditText.getText().toString().trim();
                    String foodItem = secondEditText.getText().toString().trim();

                    // Check if both values are present
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

    // Sums the values from the first EditText of each row and updates the cal field
    private void sumCalories() {
        int totalCalories = 0;

        for (int i = 0; i < container.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) container.getChildAt(i);

            // Ensure the row has at least one EditText
            if (row.getChildCount() > 0) {
                EditText firstEditText = (EditText) row.getChildAt(0);

                try {
                    int value = Integer.parseInt(firstEditText.getText().toString().trim());
                    totalCalories += value;
                } catch (NumberFormatException e) {
                    // Ignore invalid numbers
                }
            }
        }

        meal.setCal(totalCalories);
        Toast.makeText(this, "Total Calories: " + totalCalories, Toast.LENGTH_SHORT).show();
    }
}