package com.example.myapplication.screens;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        // קבלת המשתמש המחובר
        User user = SharedPreferencesUtil.getUser(getApplicationContext());

        if (user != null) {
            // הצגת פרטי המשתמש
            tvEmail.setText("Email: " + user.getEmail());
            tvPhone.setText("Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A"));
            tvDisplayName.setText("Name: " + (user.getFname() != null ? user.getFname() : "N/A"));
        } else {
            tvEmail.setText("User not logged in");
        }
    }
}