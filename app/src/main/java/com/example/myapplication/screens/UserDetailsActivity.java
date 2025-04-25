package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.services.DatabaseService.DatabaseCallback;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView userIdTextView;
    private EditText userFnameEditText, userLnameEditText, userPhoneEditText, userEmailEditText, userDailycalEditText;
    private Button saveChangesButton, deleteUserButton, backToDeleteUserActivityButton;

    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize views
        userIdTextView = findViewById(R.id.userIdTextView);
        userFnameEditText = findViewById(R.id.userFnameEditText);
        userLnameEditText = findViewById(R.id.userLnameEditText);
        userPhoneEditText = findViewById(R.id.userPhoneEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userDailycalEditText = findViewById(R.id.userDailycalEditText);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);
        backToDeleteUserActivityButton = findViewById(R.id.backToDeleteUserActivityButton);

        // Initialize DatabaseService
        databaseService = DatabaseService.getInstance();

        // Get user object from the Intent
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            // Set user details in the TextViews and EditTexts
            userIdTextView.setText("ID: " + user.getId());
            userFnameEditText.setText(user.getFname());
            userLnameEditText.setText(user.getLname());
            userPhoneEditText.setText(user.getPhone());
            userEmailEditText.setText(user.getEmail());
            userDailycalEditText.setText(String.valueOf(user.getDailycal()));

            // Save changes functionality
            saveChangesButton.setOnClickListener(v -> {
                String updatedFname = userFnameEditText.getText().toString();
                String updatedLname = userLnameEditText.getText().toString();
                String updatedPhone = userPhoneEditText.getText().toString();
                String updatedEmail = userEmailEditText.getText().toString();
                String updatedDailycalString = userDailycalEditText.getText().toString();

                if (updatedFname.isEmpty() || updatedLname.isEmpty() || updatedPhone.isEmpty() || updatedEmail.isEmpty() || updatedDailycalString.isEmpty()) {
                    Toast.makeText(UserDetailsActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Integer updatedDailycal = Integer.parseInt(updatedDailycalString);

                    // Update the user object
                    user.setFname(updatedFname);
                    user.setLname(updatedLname);
                    user.setPhone(updatedPhone);
                    user.setEmail(updatedEmail);
                    user.setDailycal(updatedDailycal);

                    // Update user data using DatabaseService
                    databaseService.updateUser(user, new DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void result) {
                            Toast.makeText(UserDetailsActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(UserDetailsActivity.this, "Failed to update user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(UserDetailsActivity.this, "Daily calorie count must be a valid number", Toast.LENGTH_SHORT).show();
                }
            });

            // Delete user functionality
            deleteUserButton.setOnClickListener(v -> {
                String userId = user.getId();
                databaseService.deleteUser(userId, new DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void result) {
                        Toast.makeText(UserDetailsActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful deletion
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(UserDetailsActivity.this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            // Back to DeleteUserActivity
            backToDeleteUserActivityButton.setOnClickListener(v -> {
                Intent backIntent = new Intent(UserDetailsActivity.this, DeleteUserActivity.class);
                startActivity(backIntent);
                finish(); // Close the current activity
            });

        } else {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user data is available
        }
    }
}
