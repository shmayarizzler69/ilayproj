package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.core.app.NotificationCompat;
import android.os.Build;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.utils.SharedPreferencesUtil;
import com.example.myapplication.utils.Validator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String CHANNEL_ID = "welcome_channel";
    private static final int NOTIFICATION_ID = 1;

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnGoLog;

    private AuthenticationService authenticationService;
    private DatabaseService databaseService;
    private User user = null;

    // פרטי מנהל
    private final String ADMIN_EMAIL = "admin@gmail.com";
    private final String ADMIN_PASSWORD = "admin123";

    public static boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authenticationService = AuthenticationService.getInstance();
        databaseService = DatabaseService.getInstance();

        user = SharedPreferencesUtil.getUser(LoginActivity.this);

        initViews();
        setupListeners();

        if (user != null) {
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
        }
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnGoLog = findViewById(R.id.btnLog);
    }

    private void setupListeners() {
        btnGoLog.setOnClickListener(this);

        // Clear errors on text change
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilEmail.setError(null);
            }
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilPassword.setError(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnGoLog.getId()) {
            Log.d(TAG, "onClick: Login button clicked");

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            Log.d(TAG, "onClick: Email: " + email);
            Log.d(TAG, "onClick: Password: " + password);

            if (!checkInput(email, password)) return;

            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                // כניסת מנהל
                isAdmin = true;
                Log.d(TAG, "onClick: Admin logged in");
                Intent adminIntent = new Intent(LoginActivity.this, AdminPage.class);
                startActivity(adminIntent);
            } else {
                // כניסת משתמש
                Log.d(TAG, "onClick: Logging in user...");
                loginUser(email, password);
            }
        }
    }

    private boolean checkInput(String email, String password) {
        boolean isValid = true;

        if (!Validator.isEmailValid(email)) {
            Log.e(TAG, "checkInput: Invalid email address");
            tilEmail.setError("כתובת אימייל לא תקינה");
            isValid = false;
        }

        if (!Validator.isPasswordValid(password)) {
            Log.e(TAG, "checkInput: Password must be at least 6 characters long");
            tilPassword.setError("הסיסמה חייבת להכיל לפחות 6 תווים");
            isValid = false;
        }

        return isValid;
    }

    private void loginUser(String email, String password) {
        btnGoLog.setEnabled(false);
        
        authenticationService.signIn(email, password, new AuthenticationService.AuthCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "onCompleted: User logged in successfully");
                databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User user) {
                        Log.d(TAG, "onCompleted: User data retrieved successfully");
                        SharedPreferencesUtil.saveUser(LoginActivity.this, user);

                        // Show welcome notification
                        showWelcomeNotification(user.getFname(), user.getLname());

                        Intent mainIntent = new Intent(LoginActivity.this, AfterLoginMain.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                        tilPassword.setError("אימייל או סיסמה לא נכונים");
                        btnGoLog.setEnabled(true);
                        authenticationService.signOut();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Log.w(TAG, "signInWithEmail:failure", e);
                Toast.makeText(getApplicationContext(), "ההתחברות נכשלה.", Toast.LENGTH_SHORT).show();
                btnGoLog.setEnabled(true);
            }
        });
    }



    private void showWelcomeNotification(String firstName, String lastName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("ברוך שובך!")
                .setContentText("שלום " + firstName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
