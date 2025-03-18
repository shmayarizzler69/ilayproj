package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.SharedPreferencesUtil;

public class infouser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infouser);

        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvDisplayName = findViewById(R.id.tvDisplayName);
        TextView tvLastName = findViewById(R.id.tvLastName);
        TextView tvUserId = findViewById(R.id.tvUserId);
        TextView tvDailyCal = findViewById(R.id.tvDailyCal); // New TextView for daily calorie limit
        Button btnBack = findViewById(R.id.btnBack);

        // Retrieve the logged-in user
        User user = SharedPreferencesUtil.getUser(getApplicationContext());

        if (user != null) {
            // Display user details
            tvEmail.setText("Email: " + user.getEmail());
            tvPhone.setText("Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A"));
            tvDisplayName.setText("Name: " + (user.getFname() != null ? user.getFname() : "N/A"));
            tvLastName.setText("Last Name: " + (user.getLname() != null ? user.getLname() : "N/A"));
            tvUserId.setText("User ID: " + user.getId());
            tvDailyCal.setText("Daily Calorie Limit: " + (user.getDailycal() != null ? user.getDailycal() : "N/A"));
        } else {
            tvEmail.setText("User not logged in");
        }

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(infouser.this, AfterLoginMain.class);
            startActivity(intent);
        });
    }
}
