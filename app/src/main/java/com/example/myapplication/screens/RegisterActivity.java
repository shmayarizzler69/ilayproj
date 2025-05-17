package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.utils.SharedPreferencesUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/// Activity for registering the user
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_DAILY_CALORIES = 1000;

    private TextInputLayout tilFName, tilLName, tilPhone, tilEmail, tilPassword, tilDailyCal;
    private TextInputEditText etFName, etLName, etPhone, etEmail, etPassword, etDailyCal;
    private MaterialButton btnRegister;
    private CircularProgressIndicator progressIndicator;

    private AuthenticationService authenticationService;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeServices();
        setupClickListeners();
    }

    private void initializeViews() {
        // TextInputLayouts
        tilFName = findViewById(R.id.tilFName);
        tilLName = findViewById(R.id.tilLName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilDailyCal = findViewById(R.id.tilDailyCal);

        // TextInputEditTexts
        etFName = findViewById(R.id.etFname);
        etLName = findViewById(R.id.etLname);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDailyCal = findViewById(R.id.etDailyCal);

        btnRegister = findViewById(R.id.btnReg);
        progressIndicator = findViewById(R.id.progressIndicator);
    }

    private void initializeServices() {
        authenticationService = AuthenticationService.getInstance();
        databaseService = DatabaseService.getInstance();
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> validateAndRegister());
    }

    private void validateAndRegister() {
        // Reset all errors
        resetErrors();

        // Get all input values
        String firstName = etFName.getText().toString().trim();
        String lastName = etLName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String dailyCalStr = etDailyCal.getText().toString().trim();

        // Validate all fields
        boolean isValid = true;

        if (TextUtils.isEmpty(firstName) || firstName.length() < 2) {
            tilFName.setError("שם פרטי חייב להכיל לפחות 2 תווים");
            isValid = false;
        }

        if (TextUtils.isEmpty(lastName) || lastName.length() < 2) {
            tilLName.setError("שם משפחה חייב להכיל לפחות 2 תווים");
            isValid = false;
        }

        if (TextUtils.isEmpty(phone) || !isValidPhoneNumber(phone)) {
            tilPhone.setError("מספר טלפון לא תקין");
            isValid = false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("כתובת אימייל לא תקינה");
            isValid = false;
        }

        if (TextUtils.isEmpty(password) || password.length() < MIN_PASSWORD_LENGTH) {
            tilPassword.setError("הסיסמה חייבת להכיל לפחות " + MIN_PASSWORD_LENGTH + " תווים");
            isValid = false;
        } else if (password.length() > MAX_PASSWORD_LENGTH) {
            tilPassword.setError("הסיסמה לא יכולה להכיל יותר מ-" + MAX_PASSWORD_LENGTH + " תווים");
            isValid = false;
        }

        int dailyCal = 0;
        try {
            if (!TextUtils.isEmpty(dailyCalStr)) {
                dailyCal = Integer.parseInt(dailyCalStr);
                if (dailyCal < MIN_DAILY_CALORIES) {
                    tilDailyCal.setError("יעד הקלוריות היומי חייב להיות לפחות " + MIN_DAILY_CALORIES);
                    isValid = false;
                }
            } else {
                tilDailyCal.setError("נא להזין יעד קלוריות יומי");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            tilDailyCal.setError("נא להזין מספר תקין");
            isValid = false;
        }

        if (isValid) {
            showLoading(true);
            registerUser(firstName, lastName, phone, email, password, dailyCal);
        }
    }

    private void registerUser(String firstName, String lastName, String phone, String email, 
                            String password, int dailyCal) {
        authenticationService.signUp(email, password, new AuthenticationService.AuthCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "User registration successful with UID: " + uid);
                
                User newUser = new User(uid, firstName, lastName, phone, email, password, dailyCal);
                saveUserToDatabase(newUser);
            }

            @Override
            public void onFailed(Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    String errorMessage = "ההרשמה נכשלה: " + e.getMessage();
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Registration failed", e);
                });
            }
        });
    }

    private void saveUserToDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                runOnUiThread(() -> {
                    showLoading(false);
                    SharedPreferencesUtil.saveUser(RegisterActivity.this, user);
                    navigateToMainActivity();
                    Log.d(TAG, "User data saved successfully");
                });
            }

            @Override
            public void onFailed(Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    String errorMessage = "שמירת פרטי המשתמש נכשלה: " + e.getMessage();
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to save user data", e);
                });
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.length() >= 9 && phone.length() <= 10 && TextUtils.isDigitsOnly(phone);
    }

    private void resetErrors() {
        tilFName.setError(null);
        tilLName.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilDailyCal.setError(null);
    }

    private void showLoading(boolean show) {
        btnRegister.setEnabled(!show);
        if (show) {
            progressIndicator.show();
        } else {
            progressIndicator.hide();
        }
    }
}
