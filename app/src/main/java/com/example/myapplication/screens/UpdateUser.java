package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.utils.SharedPreferencesUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

// מסך עדכון פרטי משתמש - מאפשר למשתמש לעדכן את הפרטים האישיים שלו
public class UpdateUser extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etPhone, etDailyCalories;
    private TextInputEditText etWeight, etHeight, etAge;
    private AutoCompleteTextView spinnerGender;
    private MaterialButton btnUpdate, btnReturn;
    private DatabaseService databaseService;
    private String userId;

    // פונקציה שמופעלת כשהמסך נפתח בפעם הראשונה - מכינה את כל השדות עם הפרטים הנוכחיים
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
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etAge = findViewById(R.id.etAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnReturn = findViewById(R.id.btnReturn);

        // Set up gender dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_dropdown_item_1line);
        spinnerGender.setAdapter(adapter);

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

    // פונקציה שמטפלת בלחיצה על כפתור החזרה בסרגל העליון
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // פונקציה שטוענת את פרטי המשתמש הנוכחיים מהמסד נתונים
    private void loadUserDetails() {
        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    etFirstName.setText(user.getFname());
                    etLastName.setText(user.getLname());
                    etPhone.setText(user.getPhone());
                    etDailyCalories.setText(user.getDailycal() != null ? String.valueOf(user.getDailycal()) : "");
                    etWeight.setText(user.getWeight() != null ? String.valueOf(user.getWeight()) : "");
                    etHeight.setText(user.getHeight() != null ? String.valueOf(user.getHeight()) : "");
                    etAge.setText(user.getAge() != null ? String.valueOf(user.getAge()) : "");
                    
                    // Set gender selection
                    if (user.getGender() != null) {
                        spinnerGender.setText(user.getGender(), false);
                    }
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

    // פונקציה שמעדכנת את פרטי המשתמש במסד הנתונים
    private void updateUser() {
        // Get input values
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dailyCalInput = etDailyCalories.getText().toString().trim();
        String weightInput = etWeight.getText().toString().trim();
        String heightInput = etHeight.getText().toString().trim();
        String ageInput = etAge.getText().toString().trim();
        String gender = spinnerGender.getText().toString();

        // Validate inputs
        if (!validateInputs(firstName, lastName, phone, dailyCalInput, weightInput, heightInput, ageInput)) {
            return;
        }

        // Create updated user object
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFname(firstName);
        updatedUser.setLname(lastName);
        updatedUser.setPhone(phone);
        updatedUser.setDailycal(Integer.parseInt(dailyCalInput));
        updatedUser.setWeight(Double.parseDouble(weightInput));
        updatedUser.setHeight(Double.parseDouble(heightInput));
        updatedUser.setAge(Integer.parseInt(ageInput));
        updatedUser.setGender(gender);

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
                currentUser.setWeight(Double.parseDouble(weightInput));
                currentUser.setHeight(Double.parseDouble(heightInput));
                currentUser.setAge(Integer.parseInt(ageInput));
                currentUser.setGender(gender);
                SharedPreferencesUtil.saveUser(UpdateUser.this, currentUser);

                // Show success and finish
                runOnUiThread(() -> {
                    Toast.makeText(UpdateUser.this, "הפרטים עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateUser.this, AfterLoginMain.class);
                    startActivity(intent);
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

    // פונקציה שבודקת את תקינות כל השדות לפני העדכון
    private boolean validateInputs(String firstName, String lastName, String phone, String dailyCalInput,
                                 String weightInput, String heightInput, String ageInput) {
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

        // Validate weight
        if (TextUtils.isEmpty(weightInput)) {
            etWeight.setError("נדרש משקל");
            etWeight.requestFocus();
            return false;
        }
        try {
            double weight = Double.parseDouble(weightInput);
            if (weight < 30 || weight > 300) {
                etWeight.setError("משקל חייב להיות בין 30 ל-300 ק\"ג");
                etWeight.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etWeight.setError("משקל חייב להיות מספר");
            etWeight.requestFocus();
            return false;
        }

        // Validate height
        if (TextUtils.isEmpty(heightInput)) {
            etHeight.setError("נדרש גובה");
            etHeight.requestFocus();
            return false;
        }
        try {
            double height = Double.parseDouble(heightInput);
            if (height < 100 || height > 250) {
                etHeight.setError("גובה חייב להיות בין 100 ל-250 ס\"מ");
                etHeight.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etHeight.setError("גובה חייב להיות מספר");
            etHeight.requestFocus();
            return false;
        }

        // Validate age
        if (TextUtils.isEmpty(ageInput)) {
            etAge.setError("נדרש גיל");
            etAge.requestFocus();
            return false;
        }
        try {
            int age = Integer.parseInt(ageInput);
            if (age < 12 || age > 120) {
                etAge.setError("גיל חייב להיות בין 12 ל-120");
                etAge.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etAge.setError("גיל חייב להיות מספר");
            etAge.requestFocus();
            return false;
        }

        return true;
    }

    // פונקציה שמציגה הודעת שגיאה למשתמש
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
