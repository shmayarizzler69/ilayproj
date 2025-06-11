package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.services.AuthenticationService;

// מסך פתיחה - מוצג בפתיחת האפליקציה ובודק אם המשתמש מחובר
public class Splash extends AppCompatActivity {

    private static final String TAG = "Splash";
    private static final int SPLASH_DISPLAY_TIME = 3000; // 3 seconds

    AuthenticationService authenticationService;

    // פונקציה שמופעלת כשהמסך נפתח בפעם הראשונה - מציגה את מסך הפתיחה ובודקת אם המשתמש מחובר
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// get the instance of the authentication service
        authenticationService = AuthenticationService.getInstance();

        // פונקציה שמריצה את הבדיקה ברקע ומעבירה למסך המתאים
        Thread splashThread = new Thread(() -> {
            try {
                Thread.sleep(SPLASH_DISPLAY_TIME); // SPLASH_DISPLAY_TIME delay
            } catch (InterruptedException ignored) {
            } finally {
                // go to the correct activity after the delay
                Intent intent;
                /// Check if user is signed in or not and redirect to LandingActivity if not signed in
                if (authenticationService.isUserSignedIn()) {
                    Log.d(TAG, "User signed in, redirecting to AfterLoginMain ");
                    intent = new Intent(Splash.this, AfterLoginMain.class);
                } else {
                    Log.d(TAG, "User not signed in, redirecting to MainActivity");
                    intent = new Intent(Splash.this, MainActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        splashThread.start();
    }
}