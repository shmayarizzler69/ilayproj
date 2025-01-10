package com.example.myapplication.screens;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
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

/// Activity for registering the user
/// This activity is used to register the user
/// It contains fields for the user to enter their information
/// It also contains a button to register the user
/// When the user is registered, they are redirected to the main activity
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private EditText etEmail, etPassword, etFName, etLName, etPhone;
    private Button btnRegister;
    private AuthenticationService authenticationService;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /// set the layout for the activity
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// get the instance of the authentication service
        authenticationService = AuthenticationService.getInstance();
        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();

        /// get the views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFName = findViewById(R.id.etFname);
        etLName = findViewById(R.id.etLname);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnReg);

        /// set the click listener
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegister.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            /// get the input from the user
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String fName = etFName.getText().toString();
            String lName = etLName.getText().toString();
            String phone = etPhone.getText().toString();

            /// log the input
            Log.d(TAG, "onClick: Email: " + email);
            Log.d(TAG, "onClick: Password: " + password);
            Log.d(TAG, "onClick: First Name: " + fName);
            Log.d(TAG, "onClick: Last Name: " + lName);
            Log.d(TAG, "onClick: Phone: " + phone);



            /// Validate input
            Log.d(TAG, "onClick: Validating input...");
            if (!checkInput(email, password, fName, lName, phone)) {
                /// stop if input is invalid
                return;
            }

            Log.d(TAG, "onClick: Registering user...");

            /// Register user
            registerUser(email, password, fName, lName, phone);
        }
    }

    /// Check if the input is valid
    /// @return true if the input is valid, false otherwise
    /// @see Validator
    private boolean checkInput(String email, String password, String fName, String lName, String phone) {

        if (!Validator.isEmailValid(email)) {
            Log.e(TAG, "checkInput: Invalid email address");
            /// show error message to user
            etEmail.setError("Invalid email address");
            /// set focus to email field
            etEmail.requestFocus();
            return false;
        }

        if (!Validator.isPasswordValid(password)) {
            Log.e(TAG, "checkInput: Password must be at least 8 characters long");
            /// show error message to user
            etPassword.setError("Password must be at least 8 characters long");
            /// set focus to password field
            etPassword.requestFocus();
            return false;
        }

        if (!Validator.isNameValid(fName)) {
            Log.e(TAG, "checkInput: First name must be at least 3 characters long");
            /// show error message to user
            etFName.setError("First name must be at least 3 characters long");
            /// set focus to first name field
            etFName.requestFocus();
            return false;
        }

        if (!Validator.isNameValid(lName)) {
            Log.e(TAG, "checkInput: Last name must be at least 3 characters long");
            /// show error message to user
            etLName.setError("Last name must be at least 3 characters long");
            /// set focus to last name field
            etLName.requestFocus();
            return false;
        }

        if (!Validator.isPhoneValid(phone)) {
            Log.e(TAG, "checkInput: Phone number must be at least 10 characters long");
            /// show error message to user
            etPhone.setError("Phone number must be at least 10 characters long");
            /// set focus to phone field
            etPhone.requestFocus();
            return false;
        }

        Log.d(TAG, "checkInput: Input is valid");
        return true;
    }

    /// Register the user
    private void registerUser(String email, String password, String fName, String lName, String phone) {
        Log.d(TAG, "registerUser: Registering user...");

        /// call the sign up method of the authentication service
        authenticationService.signUp(email, password, new AuthenticationService.AuthCallback<String>() {

            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "onCompleted: User registered successfully");
                /// create a new user object
                User user = new User();
                user.setId(uid);
                user.setEmail(email);
                user.setPassword(password);
                user.setFname(fName);
                user.setLname(lName);
                user.setPhone(phone);



                /// call the createNewUser method of the database service
                databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {

                    @Override
                    public void onCompleted(Void v) {
                        Log.d(TAG, "onCompleted: User registered successfully");
                        /// save the user to shared preferences
                        SharedPreferencesUtil.saveUser(RegisterActivity.this, user);
                        Log.d(TAG, "onCompleted: Redirecting to MainActivity");
                        /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        /// clear the back stack (clear history) and start the MainActivity
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                    }


                    @Override
                    public void onFailed(Exception e) {
                        Log.e(TAG, "onFailed: Failed to register user", e);
                        /// show error message to user
                        Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                        /// sign out the user if failed to register
                        /// this is to prevent the user from being logged in again
                        authenticationService.signOut();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to register user", e);
                /// show error message to user
                Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
            }
        });


    }
}