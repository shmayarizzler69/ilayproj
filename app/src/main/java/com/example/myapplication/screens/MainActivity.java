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
import com.example.myapplication.models.User;
import com.example.myapplication.models.Day;
import com.example.myapplication.utils.NotificationHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

        requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        // Handle the case where permission is not granted
                    }
                });

        if (!NPH.hasNP(this)) {
            NPH.requestNP(requestNotificationPermissionLauncher);
        }

        // Fetch data from Firebase and check calories
        fetchUserDataAndCheckCalories();
    }

    private void fetchUserDataAndCheckCalories() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child("userId"); // Replace "userId" with actual user ID
        DatabaseReference dayRef = database.getReference("Days").child("currentDayId"); // Replace "currentDayId" with today's date or ID

        // Fetch user data
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // Fetch day data
                    dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot daySnapshot) {
                            Day day = daySnapshot.getValue(Day.class);
                            if (day != null) {
                                checkDailyCalories(user, day); // Check if the calorie intake exceeds the limit
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any error in fetching day data
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any error in fetching user data
            }
        });
    }

    private void checkDailyCalories(User user, Day day) {
        if (user.getDailycal() != null && day.getSumcal() > user.getDailycal()) {
            // Trigger notification
            String title = "Calorie Alert";
            String message = "Your daily calorie intake has exceeded your set limit!";
            NotificationHelper.sendNotification(this, title, message);
        }
    }

    private void initViews() {
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btntest = findViewById(R.id.btntest);
        btntest.setOnClickListener(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(this);
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
