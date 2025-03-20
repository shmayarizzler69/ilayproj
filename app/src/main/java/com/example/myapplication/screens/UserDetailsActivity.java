package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetailsActivity extends AppCompatActivity {
    private TextView userIdTextView;
    private TextView userNameTextView;
    private TextView userPhoneTextView;
    private TextView userEmailTextView;
    private TextView userDailycalTextView;
    private Button deleteUserButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize views
        userIdTextView = findViewById(R.id.userIdTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        userPhoneTextView = findViewById(R.id.userPhoneTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userDailycalTextView = findViewById(R.id.userDailycalTextView);
        deleteUserButton = findViewById(R.id.deleteUserButton);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Get user object from the Intent
        Intent intent = getIntent();
        User user = intent.getSerializableExtra("user", User.class);

        if (user != null) {
            // Set user details in the TextViews
            userIdTextView.setText("ID: " + user.getId());
            userNameTextView.setText("Name: " + user.getFname() + " " + user.getLname());
            userPhoneTextView.setText("Phone: " + user.getPhone());
            userEmailTextView.setText("Email: " + user.getEmail());
            userDailycalTextView.setText("Daily Calories: " + user.getDailycal());

            // Delete user functionality
            deleteUserButton.setOnClickListener(v -> {
                String userId = user.getId();
                databaseReference.child(userId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserDetailsActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful deletion
                    } else {
                        Toast.makeText(UserDetailsActivity.this, "Failed to delete user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user data is available
        }
    }
}
