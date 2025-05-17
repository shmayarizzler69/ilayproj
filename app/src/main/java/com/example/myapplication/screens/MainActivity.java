package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.utils.NPH;
import com.example.myapplication.utils.NotificationHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnRegister, btnLogin, btnAbout, btntest;

    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initNotificationPermission();
    }

    private void initViews() {
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(this);
    }

    private void initNotificationPermission() {
        // Register the permission request launcher
        requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Notification();
                    }
                    // Handle cases where the user denies the permission if necessary
                });

        // Check and request notification permission if needed
        if (NPH.hasNP(this)) {
            Notification();
        } else {
            NPH.requestNP(requestNotificationPermissionLauncher);
        }
    }

    private void Notification() {
        NotificationHelper.sendNotification(
                this,
                "יואו",
                "ברוך הבא לאפליקציה!"
        );
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegister) {
            Intent goReg = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(goReg);
        }
        if (v == btntest) {
            Intent goReg = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(goReg);
        }
        if (v == btnLogin) {
            Intent golog = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(golog);
        }
    }
}
