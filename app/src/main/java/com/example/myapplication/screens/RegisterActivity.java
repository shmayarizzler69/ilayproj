package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Spinner;
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

// מסך הרשמה - מאפשר למשתמש חדש להירשם למערכת עם כל הפרטים האישיים שלו
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_DAILY_CALORIES = 1000;
    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 120;

    private TextInputLayout tilFName, tilLName, tilPhone, tilEmail, tilPassword, tilDailyCal, tilHeight, tilWeight, tilAge;
    private TextInputEditText etFName, etLName, etPhone, etEmail, etPassword, etDailyCal, etHeight, etWeight, etAge;
    private Spinner spinnerGender;
    private Spinner spinnerActivity;
    private MaterialButton btnRegister;
    private CircularProgressIndicator progressIndicator;
    private MaterialButton btnCalculateCalories;

    private AuthenticationService authenticationService;
    private DatabaseService databaseService;

    // פונקציה שמופעלת כשהמסך נפתח בפעם הראשונה - מכינה את כל השדות לקליטת פרטי המשתמש
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

    // פונקציה שמאתחלת את כל השדות והכפתורים במסך
    private void initializeViews() {
        // TextInputLayouts
        tilFName = findViewById(R.id.tilFName);
        tilLName = findViewById(R.id.tilLName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilDailyCal = findViewById(R.id.tilDailyCal);
        tilHeight = findViewById(R.id.tilHeight);
        tilWeight = findViewById(R.id.tilWeight);
        tilAge = findViewById(R.id.tilAge);

        // TextInputEditTexts
        etFName = findViewById(R.id.etFname);
        etLName = findViewById(R.id.etLname);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDailyCal = findViewById(R.id.etDailyCal);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etAge = findViewById(R.id.etAge);

        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerActivity = findViewById(R.id.spinnerActivity);
        btnRegister = findViewById(R.id.btnReg);
        progressIndicator = findViewById(R.id.progressIndicator);
        btnCalculateCalories = findViewById(R.id.btnCalculateCalories);
    }

    // פונקציה שמאתחלת את השירותים הנדרשים (התחברות ומסד נתונים)
    private void initializeServices() {
        authenticationService = AuthenticationService.getInstance();
        databaseService = DatabaseService.getInstance();
    }

    // פונקציה שמגדירה מה קורה בלחיצה על הכפתורים
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> validateAndRegister());
        btnCalculateCalories.setOnClickListener(v -> calculateRecommendedCalories());
    }

    // פונקציה שמחשבת את מקדם הפעילות הגופנית לחישוב הקלוריות
    private double getActivityMultiplier(int activityPosition) {
        switch (activityPosition) {
            case 0: // לא פעיל
                return 1.2;
            case 1: // מעט פעיל
                return 1.375;
            case 2: // פעיל בינוני
                return 1.55;
            case 3: // פעיל מאוד
                return 1.725;
            case 4: // פעיל במיוחד
                return 1.9;
            default:
                return 1.55; // אופציה דיפולט זה בינוני
        }
    }

    // פונקציה שמחשבת את כמות הקלוריות היומית המומלצת לפי הנתונים שהוזנו
    private void calculateRecommendedCalories() {
        // Reset errors for relevant fields
        tilHeight.setError(null);
        tilWeight.setError(null);
        tilAge.setError(null);

        // Get input values
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        int activityPosition = spinnerActivity.getSelectedItemPosition();

        // Validate inputs
        if (TextUtils.isEmpty(heightStr)) {
            tilHeight.setError("נא להזין גובה");
            return;
        }

        if (TextUtils.isEmpty(weightStr)) {
            tilWeight.setError("נא להזין משקל");
            return;
        }

        if (TextUtils.isEmpty(ageStr)) {
            tilAge.setError("נא להזין גיל");
            return;
        }

        try {
            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);
            int age = Integer.parseInt(ageStr);

            // Validate ranges
            if (height <= 0 || height > 300) {
                tilHeight.setError("נא להזין גובה תקין");
                return;
            }

            if (weight <= 0 || weight > 500) {
                tilWeight.setError("נא להזין משקל תקין");
                return;
            }

            if (age < MIN_AGE || age > MAX_AGE) {
                tilAge.setError("נא להזין גיל תקין (בין " + MIN_AGE + " ל-" + MAX_AGE + ")");
                return;
            }

            // נוסחת BMR
            // BMR = (10 × weight) + (6.25 × height) - (5 × age) + s
            // where s is +5 for males and -161 for females
            double bmr;
            if (gender.equals("זכר")) {
                bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
            } else {
                bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
            }

            // Get activity multiplier based on selected activity level
            double activityMultiplier = getActivityMultiplier(activityPosition);

            // Calculate total daily calories
            int recommendedCalories = (int) Math.round(bmr * activityMultiplier);

            // Set the calculated value to the daily calories field
            etDailyCal.setText(String.valueOf(recommendedCalories));
            Toast.makeText(this, "הקלוריות היומיות המומלצות חושבו בהצלחה", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "נא להזין מספרים תקינים", Toast.LENGTH_SHORT).show();
        }
    }

    // פונקציה שבודקת את תקינות כל השדות ומבצעת הרשמה
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
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();

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

        Double height = null;
        try {
            if (!TextUtils.isEmpty(heightStr)) {
                height = Double.parseDouble(heightStr);
                if (height <= 0 || height > 300) {
                    tilHeight.setError("נא להזין גובה תקין");
                    isValid = false;
                }
            } else {
                tilHeight.setError("נא להזין גובה");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            tilHeight.setError("נא להזין מספר תקין");
            isValid = false;
        }

        Double weight = null;
        try {
            if (!TextUtils.isEmpty(weightStr)) {
                weight = Double.parseDouble(weightStr);
                if (weight <= 0 || weight > 500) {
                    tilWeight.setError("נא להזין משקל תקין");
                    isValid = false;
                }
            } else {
                tilWeight.setError("נא להזין משקל");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            tilWeight.setError("נא להזין מספר תקין");
            isValid = false;
        }

        // Validate age
        int age = 0;
        try {
            if (!TextUtils.isEmpty(ageStr)) {
                age = Integer.parseInt(ageStr);
                if (age < MIN_AGE || age > MAX_AGE) {
                    tilAge.setError("נא להזין גיל תקין (בין " + MIN_AGE + " ל-" + MAX_AGE + ")");
                    isValid = false;
                }
            } else {
                tilAge.setError("נא להזין גיל");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            tilAge.setError("נא להזין מספר תקין");
            isValid = false;
        }

        if (isValid) {
            showLoading(true);
            registerUser(firstName, lastName, phone, email, password, dailyCal, gender, height, weight, age);
        }
    }

    // פונקציה שרושמת את המשתמש החדש במערכת ההתחברות
    private void registerUser(String firstName, String lastName, String phone, String email, 
                            String password, int dailyCal, String gender, Double height, Double weight, int age) {
        authenticationService.signUp(email, password, new AuthenticationService.AuthCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "User registration successful with UID: " + uid);
                
                User newUser = new User(uid, firstName, lastName, phone, email, password, dailyCal, gender, height, weight, age);
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

    // פונקציה ששומרת את פרטי המשתמש במסד הנתונים
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

    // פונקציה שמעבירה למסך הראשי אחרי הרשמה מוצלחת
    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // פונקציה שבודקת אם מספר הטלפון תקין
    private boolean isValidPhoneNumber(String phone) {
        return phone.length() >= 9 && phone.length() <= 10 && TextUtils.isDigitsOnly(phone);
    }

    // פונקציה שמנקה את כל הודעות השגיאה מהשדות
    private void resetErrors() {
        tilFName.setError(null);
        tilLName.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilDailyCal.setError(null);
        tilHeight.setError(null);
        tilWeight.setError(null);
        tilAge.setError(null);
    }

    // פונקציה שמציגה או מסתירה את אנימציית הטעינה
    private void showLoading(boolean show) {
        btnRegister.setEnabled(!show);
        if (show) {
            progressIndicator.show();
        } else {
            progressIndicator.hide();
        }
    }
}
