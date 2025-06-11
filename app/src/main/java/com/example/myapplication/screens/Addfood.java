package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.example.myapplication.R;
import com.example.myapplication.models.MyDate;
import com.example.myapplication.models.Day;
import com.example.myapplication.models.Meal;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

// מסך להוספת ארוחה חדשה - כאן אפשר להוסיף מאכלים והקלוריות שלהם
public class AddFood extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddFood";
    private static final String[] MEAL_TYPES = {"Breakfast", "Lunch", "Dinner", "Snack", "Other"};
    
    private static final int FOOD_NAME_WEIGHT = 2;
    private static final int CALORIES_WEIGHT = 1;
    private static final int MARGIN_8DP = 8;
    private static final float ELEVATION_4DP = 4f;
    
    private LinearLayout container;
    private Meal meal;
    private HashMap<Integer, String> foodInputs;
    private Button btnBack;
    private AuthenticationService authenticationService;
    private DatabaseService databaseService;
    private String currentUserId;
    private Day existingDay;

    // פונקציה שמופעלת כשהמסך נפתח בפעם הראשונה - מכינה את כל הכפתורים והשדות שנראה במסך
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood);
        
        initializeFields();
        setupButtons();
        setupMealTypeSpinner();
        
        // מתחיל עם שורה
        addNewRowWithTwoEditTexts();

        // Get the existing day if passed from DayDetailActivity
        existingDay = (Day) getIntent().getSerializableExtra("day");
        if (existingDay != null) {
            btnBack.setOnClickListener(v -> finish()); // Go back to DayDetailActivity
        }
    }

    // פונקציה שמאתחלת את כל המשתנים הבסיסיים שנצטרך במסך
    private void initializeFields() {
        meal = new Meal();
        container = findViewById(R.id.container);
        foodInputs = new HashMap<>();
        btnBack = findViewById(R.id.btnback);
        
        authenticationService = AuthenticationService.getInstance();
        databaseService = DatabaseService.getInstance();
        currentUserId = authenticationService.getCurrentUserId();
    }

    // פונקציה שמגדירה את כל הכפתורים במסך ומה יקרה כשלוחצים עליהם
    private void setupButtons() {
        btnBack.setOnClickListener(this);
        
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> handleSubmit());

        Button addRowButton = findViewById(R.id.addRowButton);
        addRowButton.setOnClickListener(v -> addNewRowWithTwoEditTexts());

        Button removeRowButton = findViewById(R.id.removeRowButton);
        removeRowButton.setOnClickListener(v -> removeLastRow());
    }

    // פונקציה שמגדירה את התפריט הנפתח לבחירת סוג הארוחה (ארוחת בוקר, צהריים וכו')
    private void setupMealTypeSpinner() {
        Spinner mealTypeSpinner = findViewById(R.id.mealTypeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, MEAL_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);
        
        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMealType = MEAL_TYPES[position];
                meal.setDetail(selectedMealType);
                Toast.makeText(AddFood.this, "Selected meal type: " + selectedMealType, 
                    Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    // פונקציה שמטפלת בשמירת הארוחה החדשה - בודקת שהכל תקין ושולחת למסד הנתונים
    private void handleSubmit() {
        String mealId = databaseService.generateMealId();
        meal.setMealId(mealId);
        meal.setCal(calculateTotalCalories());

        if (existingDay != null) {
            // Update existing day
            existingDay.addMeal(meal);
            databaseService.updateDay(existingDay, currentUserId, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void result) {
                    Toast.makeText(AddFood.this, "Meal added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to DayDetailActivity
                }

                @Override
                public void onFailed(Exception exception) {
                    Toast.makeText(AddFood.this, "Failed to add meal: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new day or update existing day for current date
            MyDate currentMyDate = new MyDate(getCurrentDate());
            databaseService.searchDayByDate(currentMyDate, currentUserId, new DatabaseService.DatabaseCallback<Day>() {
                @Override
                public void onCompleted(@Nullable Day day) {
                    handleDaySearchResult(day, currentMyDate);
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(AddFood.this, "Failed to search for day", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to search for day", e);
                }
            });
        }
    }

    private void handleDaySearchResult(Day day, MyDate currentMyDate) {
        if (day != null) {
            updateExistingDay(day);
            return;
        }

        createNewDay(currentMyDate);
    }

    private void updateExistingDay(Day day) {
        day.addMeal(meal);
        databaseService.updateDay(day, currentUserId, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "Day updated");
                Toast.makeText(AddFood.this, "Day updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AddFood.this, "Failed to update day", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to update day", e);
            }
        });
    }

    private void createNewDay(MyDate currentMyDate) {
        Day newDay = new Day();
        newDay.setDayId(databaseService.generateDayId());
        newDay.setDate(currentMyDate);
        newDay.setSumcal(0);
        newDay.addMeal(meal);

        databaseService.createNewDay(newDay, currentUserId, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "Day created successfully");
                Toast.makeText(AddFood.this, "Day created successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to create Day", e);
                Toast.makeText(AddFood.this, "Failed to create Day", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            if (existingDay != null) {
                finish(); // Return to DayDetailActivity
            } else {
                Intent goback = new Intent(getApplicationContext(), AfterLoginMain.class);
                startActivity(goback);
            }
        }
    }

    // פונקציה שמוסיפה שורה חדשה למסך - שדה למאכל ושדה לקלוריות
    private void addNewRowWithTwoEditTexts() {
        LinearLayout newRow = createRowLayout();
        EditText foodNameEditText = createFoodNameEditText();
        EditText caloriesEditText = createCaloriesEditText();
        
        setupFoodNameEditTextListener(foodNameEditText, newRow);
        
        newRow.addView(foodNameEditText);
        newRow.addView(caloriesEditText);
        
        applyRowStyling(newRow);
        container.addView(newRow);
    }

    // פונקציה שיוצרת שורה חדשה עם שני שדות טקסט
    private LinearLayout createRowLayout() {
        LinearLayout newRow = new LinearLayout(this);
        newRow.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, MARGIN_8DP, 0, MARGIN_8DP);
        newRow.setLayoutParams(rowParams);
        return newRow;
    }

    // פונקציה שיוצרת שדה טקסט להזנת שם המאכל
    private EditText createFoodNameEditText() {
        EditText foodNameEditText = new EditText(this);
        foodNameEditText.setHint(R.string.enter_food_hint);
        foodNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        foodNameEditText.setBackgroundResource(android.R.drawable.edit_text);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                FOOD_NAME_WEIGHT
        );
        params.setMargins(MARGIN_8DP, 0, MARGIN_8DP, 0);
        foodNameEditText.setLayoutParams(params);
        return foodNameEditText;
    }

    // פונקציה שיוצרת שדה טקסט להזנת הקלוריות
    private EditText createCaloriesEditText() {
        EditText caloriesEditText = new EditText(this);
        caloriesEditText.setHint(R.string.calories_hint);
        caloriesEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        caloriesEditText.setBackgroundResource(android.R.drawable.edit_text);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                CALORIES_WEIGHT
        );
        params.setMargins(MARGIN_8DP, 0, MARGIN_8DP, 0);
        caloriesEditText.setLayoutParams(params);
        return caloriesEditText;
    }

    private void setupFoodNameEditTextListener(EditText foodNameEditText, LinearLayout row) {
        foodNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFoodInput(s.toString().trim(), container.indexOfChild(row));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateFoodInput(String input, int rowIndex) {
        if (!input.isEmpty()) {
            foodInputs.put(rowIndex, input);
        } else {
            foodInputs.remove(rowIndex);
        }
        meal.setFood(new ArrayList<>(foodInputs.values()));
    }

    private void applyRowStyling(LinearLayout row) {
        row.setBackgroundResource(android.R.color.white);
        float elevationPx = ELEVATION_4DP * getResources().getDisplayMetrics().density;
        row.setElevation(elevationPx);
        int paddingPx = (int) (MARGIN_8DP * getResources().getDisplayMetrics().density);
        row.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
    }

    // פונקציה שמוחקת את השורה האחרונה מהרשימה
    private void removeLastRow() {
        int rowCount = container.getChildCount();
        if (rowCount > 0) {
            int lastRowIndex = rowCount - 1;
            container.removeViewAt(lastRowIndex);
            foodInputs.remove(lastRowIndex);
            meal.setFood(new ArrayList<>(foodInputs.values()));
            showToast(getString(R.string.row_removed));
        } else {
            showToast(getString(R.string.no_rows_to_remove));
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Date getCurrentDate() {
        return new Date();
    }

    // פונקציה שמחשבת את סך כל הקלוריות מכל המאכלים שהוזנו
    private int calculateTotalCalories() {
        int totalCalories = 0;

        for (int i = 0; i < container.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) container.getChildAt(i);

            if (row.getChildCount() >= 2) {
                EditText caloriesEditText = (EditText) row.getChildAt(1);
                String caloriesText = caloriesEditText.getText().toString().trim();

                if (!caloriesText.isEmpty()) {
                    totalCalories += Integer.parseInt(caloriesText);
                }
            }
        }

        return totalCalories;
    }
}
