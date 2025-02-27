package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnGoLog;

    private AuthenticationService authenticationService;
    private DatabaseService databaseService;
    private User user = null;

    // Admin credentials
    private final String ADMIN_EMAIL = "admin@gmail.com";
    private final String ADMIN_PASSWORD = "123456";

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

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnGoLog = findViewById(R.id.btnLog);

        btnGoLog.setOnClickListener(this);

        if (user != null) {
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
        }
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
                // Handle admin login
                isAdmin = true;
                Log.d(TAG, "onClick: Admin logged in");
                Intent adminIntent = new Intent(LoginActivity.this, AdminPage.class);
                startActivity(adminIntent);
            } else {
                // Handle regular user login
                Log.d(TAG, "onClick: Logging in user...");
                loginUser(email, password);
            }
        }
    }

    private boolean checkInput(String email, String password) {
        if (!Validator.isEmailValid(email)) {
            Log.e(TAG, "checkInput: Invalid email address");
            etEmail.setError("Invalid email address");
            etEmail.requestFocus();
            return false;
        }

        if (!Validator.isPasswordValid(password)) {
            Log.e(TAG, "checkInput: Password must be at least 6 characters long");
            etPassword.setError("Password must be at least 6 characters long");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        authenticationService.signIn(email, password, new AuthenticationService.AuthCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "onCompleted: User logged in successfully");
                databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User user) {
                        Log.d(TAG, "onCompleted: User data retrieved successfully");
                        SharedPreferencesUtil.saveUser(LoginActivity.this, user);

                        Intent mainIntent = new Intent(LoginActivity.this, AfterLoginMain.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                        etPassword.setError("Invalid email or password");
                        etPassword.requestFocus();
                        authenticationService.signOut();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Log.w(TAG, "signInWithEmail:failure", e);
                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
