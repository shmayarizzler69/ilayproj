package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;

public class UpdateUser extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextPhone;
    private Button buttonUpdate, buttonReturn;

    private DatabaseService databaseService;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        // Initialize views
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonReturn = findViewById(R.id.buttonReturn);

        // Initialize database service and get current user ID
        databaseService = DatabaseService.getInstance();
        userId = databaseService.getCurrentUserId();

        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

    private void updateUser() {
        // Get input values
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter at least one field to update.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a User object with updated fields
        User user = new User();
        user.setId(userId);
        if (!TextUtils.isEmpty(firstName)) {
            user.setFname(firstName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            user.setLname(lastName);
        }
        if (!TextUtils.isEmpty(phone)) {
            user.setPhone(phone);
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
