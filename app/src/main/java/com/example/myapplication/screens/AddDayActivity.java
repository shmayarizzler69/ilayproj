package com.example.myapplication.screens;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.example.myapplication.models.MyDate;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;

import java.util.Calendar;

// מסך להוספת יום חדש ביומן - כאן אפשר להוסיף כותרת, תיאור ותאריך
public class AddDayActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private CalendarView calendarView;
    private TextView selectedDateText;
    private DatabaseService databaseService;
    private MyDate selectedDate;

    // פונקציה שמופעלת כשהמסך נפתח בפעם הראשונה - מכינה את כל הכפתורים והשדות שנראה במסך
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Day");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        calendarView = findViewById(R.id.calendarView);
        selectedDateText = findViewById(R.id.selectedDateText);
        Button saveButton = findViewById(R.id.saveButton);

        // Initialize database service
        databaseService = DatabaseService.getInstance();

        // Set default date to today
        Calendar calendar = Calendar.getInstance();
        selectedDate = new MyDate(calendar.get(Calendar.YEAR), 
                                calendar.get(Calendar.MONTH) + 1, 
                                calendar.get(Calendar.DAY_OF_MONTH));
        updateDateDisplay();

        // Setup calendar view
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = new MyDate(year, month + 1, dayOfMonth);
            updateDateDisplay();
        });

        // Setup save button
        saveButton.setOnClickListener(v -> saveDay());
    }

    // פונקציה שמעדכנת את התצוגה של התאריך שנבחר על המסך
    private void updateDateDisplay() {
        if (selectedDate != null) {
            selectedDateText.setText(selectedDate.toString());
        }
    }

    // פונקציה ששומרת את היום החדש במערכת - בודקת שהכל תקין ושולחת למסד הנתונים
    private void saveDay() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }

        String userId = AuthenticationService.getInstance().getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if a day already exists for this date
        databaseService.searchDayByDate(selectedDate, userId, new DatabaseService.DatabaseCallback<Day>() {
            @Override
            public void onCompleted(Day existingDay) {
                if (existingDay != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddDayActivity.this, 
                            "A day already exists for this date!", 
                            Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Create new day
                Day newDay = new Day();
                newDay.setTitle(title);
                newDay.setDescription(description);
                newDay.setDate(selectedDate);
                newDay.setUserId(userId);

                // Save to database
                databaseService.addDay(newDay, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void result) {
                        Toast.makeText(AddDayActivity.this, "Day saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(AddDayActivity.this, "Failed to save day: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AddDayActivity.this, 
                    "Error checking for existing day: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    // פונקציה שמטפלת בלחיצה על כפתור החזרה בסרגל העליון
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 