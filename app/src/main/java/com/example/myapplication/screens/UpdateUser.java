package com.example.myapplication.screens;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;

public class UpdateUser extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone, etEmail;
    private Button btnUpdateUser;
    private AuthenticationService authenticationService;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        // Initialize UI elements
        etFirstName = findViewById(R.id.editTextFirstName);
        etLastName = findViewById(R.id.editTextLastName);
        btnUpdateUser = findViewById(R.id.btnUpdateUser);

        // Initialize services
        authenticationService = AuthenticationService.getInstance();
        databaseService = DatabaseService.getInstance();

        String currentUserId = authenticationService.getCurrentUserId();

        // Load existing user information
        loadUserInfo(currentUserId);

        // Update user info button click listener
        btnUpdateUser.setOnClickListener(v -> updateUserInfo(currentUserId));
    }

    private void loadUserInfo(String userId) {
        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    etFirstName.setText(user.getFname());
                    etLastName.setText(user.getLname());
                    etPhone.setText(user.getPhone());
                    etEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateUser.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo(String userId) {
        // Validate inputs
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create updated user object
        User updatedUser = new User(userId, firstName, lastName, phone, email, null);

        // Update user information in Firebase
        databaseService.updateUserField(updatedUser, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(UpdateUser.this, "User info updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateUser.this, "Failed to update user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
