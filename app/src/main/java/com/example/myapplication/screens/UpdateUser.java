package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.utils.SharedPreferencesUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UpdateUser extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etPhone, etDailyCalories;
    private MaterialButton btnUpdate, btnReturn;
    private DatabaseService databaseService;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Initialize views
        etFirstName = findViewById(R.id.ETFName);
        etLastName = findViewById(R.id.ETLName);
        etPhone = findViewById(R.id.etPhone);
        etDailyCalories = findViewById(R.id.etDC);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnReturn = findViewById(R.id.btnReturn);

        // Initialize database service and get current user ID
        databaseService = DatabaseService.getInstance();
        userId = databaseService.getCurrentUserId();

        if (userId == null) {
            Toast.makeText(this, "משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load user details
        loadUserDetails();

        // Set click listeners
        btnUpdate.setOnClickListener(v -> updateUser());
        btnReturn.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserDetails() {
        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    etFirstName.setText(user.getFname());
                    etLastName.setText(user.getLname());
                    etPhone.setText(user.getPhone());
                    etDailyCalories.setText(user.getDailycal() != null ? String.valueOf(user.getDailycal()) : "");
                } else {
                    showError("לא ניתן לטעון את פרטי המשתמש");
                }
            }

            @Override
            public void onFailed(Exception e) {
                showError("שגיאה: " + e.getMessage());
            }
        });
    }

    private void updateUser() {
        // Get input values
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dailyCalInput = etDailyCalories.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(firstName, lastName, phone, dailyCalInput)) {
            return;
        }

        // Create updated user object
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFname(firstName);
        updatedUser.setLname(lastName);
        updatedUser.setPhone(phone);
        updatedUser.setDailycal(Integer.parseInt(dailyCalInput));

        // Show loading state
        btnUpdate.setEnabled(false);
        btnUpdate.setText("מעדכן...");

        // Perform the update
        databaseService.updateUserField(updatedUser, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                // Update shared preferences
                User currentUser = SharedPreferencesUtil.getUser(UpdateUser.this);
                currentUser.setFname(firstName);
                currentUser.setLname(lastName);
                currentUser.setPhone(phone);
                currentUser.setDailycal(Integer.parseInt(dailyCalInput));
                SharedPreferencesUtil.saveUser(UpdateUser.this, currentUser);

                // Show success and finish
                runOnUiThread(() -> {
                    Toast.makeText(UpdateUser.this, "הפרטים עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailed(Exception e) {
                runOnUiThread(() -> {
                    btnUpdate.setEnabled(true);
                    btnUpdate.setText("שמור שינויים");
                    showError("שגיאה בעדכון: " + e.getMessage());
                });
            }
        });
    }

    private boolean validateInputs(String firstName, String lastName, String phone, String dailyCalInput) {
        if (firstName.length() < 2) {
            etFirstName.setError("שם פרטי קצר מדי");
            etFirstName.requestFocus();
            return false;
        }

        if (lastName.length() < 2) {
            etLastName.setError("שם משפחה קצר מדי");
            etLastName.requestFocus();
            return false;
        }

        if (phone.length() < 9 || phone.length() > 10) {
            etPhone.setError("מספר טלפון לא תקין");
            etPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(dailyCalInput)) {
            etDailyCalories.setError("נדרש יעד קלוריות יומי");
            etDailyCalories.requestFocus();
            return false;
        }

        try {
            int dailyCal = Integer.parseInt(dailyCalInput);
            if (dailyCal < 1000 || dailyCal > 10000) {
                etDailyCalories.setError("יעד קלוריות יומי חייב להיות בין 1000 ל-10000");
                etDailyCalories.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etDailyCalories.setError("יעד קלוריות יומי חייב להיות מספר");
            etDailyCalories.requestFocus();
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
