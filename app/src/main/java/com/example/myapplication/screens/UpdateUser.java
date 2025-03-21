package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.DatabaseService;

public class UpdateUser extends AppCompatActivity {

    private EditText FName, LName, Phone, DC;
    private Button buttonUpdate, buttonReturn;
    private DatabaseService databaseService;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        // Initialize views
        FName = findViewById(R.id.ETFName);
        LName = findViewById(R.id.ETLName);
        Phone = findViewById(R.id.etPhone);
        DC = findViewById(R.id.etDC);
        buttonUpdate = findViewById(R.id.btnUpdate);
        buttonReturn = findViewById(R.id.btnReturn);

        // Initialize database service and get current user ID
        databaseService = DatabaseService.getInstance();
        userId = databaseService.getCurrentUserId();

        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load user details and set them into the EditText fields
        loadUserDetails();

        // Set onClickListener for the update button
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        // Set onClickListener for the return button
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to AfterLoginMain activity
                Intent intent = new Intent(UpdateUser.this, AfterLoginMain.class);
                startActivity(intent);
                finish(); // Optional: finish this activity to remove it from the back stack
            }
        });
    }

    private void loadUserDetails() {
        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    // Set user details into the EditText fields
                    FName.setText(user.getFname());
                    LName.setText(user.getLname());
                    Phone.setText(user.getPhone());
                    DC.setText(user.getDailycal());
                } else {
                    Toast.makeText(UpdateUser.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateUser.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser() {
        // Get input values
        String fName = FName.getText().toString().trim();
        String lName = LName.getText().toString().trim();
        String phone = Phone.getText().toString().trim();
        String dailyCalInput = DC.getText().toString().trim();

        // Validate inputs
        if (fName.length() < 2) {
            Toast.makeText(this, "שם פרטי קצר מדי", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lName.length() < 2) {
            Toast.makeText(this, "שם משפחה קצר מדי", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() < 9 || phone.length() > 10) {
            Toast.makeText(this, "מספר טלפון לא הגיוני", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer dailyCal = null;
        if (!TextUtils.isEmpty(dailyCalInput)) {
            dailyCal = Integer.parseInt(dailyCalInput);
            if (dailyCal < 1000 || dailyCal > 10000) {
                Toast.makeText(this, "יעד קלוריות יומי חייב להיות הגיוני", Toast.LENGTH_SHORT).show();
                return;
                }
            }


        // Create a User object with updated fields
        User user = new User();
        user.setId(userId);
        if (!TextUtils.isEmpty(fName)) {
            user.setFname(fName);
        }
        if (!TextUtils.isEmpty(lName)) {
            user.setLname(lName);
        }
        if (!TextUtils.isEmpty(phone)) {
            user.setPhone(phone);
        }
        if (dailyCal != null) {
            user.setDailycal(String.valueOf(dailyCal));
        }

        // Perform the update
        databaseService.updateUserField(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(UpdateUser.this, "User data updated successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after update
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateUser.this, "Failed to update user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
