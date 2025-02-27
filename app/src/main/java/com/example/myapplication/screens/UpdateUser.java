package com.example.myapplication.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.utils.SharedPreferencesUtil;

public class UpdateUserActivity extends AppCompatActivity {

    private static final String TAG = "UpdateUserActivity";

    EditText etFName, etLName, etPhone, etEmail, etPassword;
    Button btnUpdate;
    DatabaseService databaseService;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        init_views();

        databaseService = DatabaseService.getInstance();
        currentUser = SharedPreferencesUtil.getUser(this);

        if (currentUser != null) {
            populateFields();
        } else {
            Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });
    }

    private void init_views() {
        etFName = findViewById(R.id.etFname);
        etLName = findViewById(R.id.etLname);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void populateFields() {
        etFName.setText(currentUser.getFname());
        etLName.setText(currentUser.getLname());
        etPhone.setText(currentUser.getPhone());
        etEmail.setText(currentUser.getEmail());
        etPassword.setText(currentUser.getPassword());
    }

    private void updateUserDetails() {
        String fName = etFName.getText().toString().trim();
        String lName = etLName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isValid = validateFields(fName, lName, phone, email, password);

        if (isValid) {
            currentUser.setFname(fName);
            currentUser.setLname(lName);
            currentUser.setPhone(phone);
            currentUser.setEmail(email);
            currentUser.setPassword(password);

            databaseService.updateUser(currentUser, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(UpdateUserActivity.this, "User updated successfully!", Toast.LENGTH_SHORT).show();
                    SharedPreferencesUtil.saveUser(UpdateUserActivity.this, currentUser);
                    finish();
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e(TAG, "Failed to update user: ", e);
                    Toast.makeText(UpdateUserActivity.this, "Failed to update user. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateFields(String fName, String lName, String phone, String email, String password) {
        boolean isValid = true;

        if (fName.length() < 2) {
            etFName.setError("First name too short");
            isValid = false;
        }
        if (lName.length() < 2) {
            etLName.setError("Last name too short");
            isValid = false;
        }
        if (phone.length() < 9 || phone.length() > 10) {
            etPhone.setError("Invalid phone number");
            isValid = false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            isValid = false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password too short");
            isValid = false;
        }
        if (password.length() > 20) {
            etPassword.setError("Password too long");
            isValid = false;
        }

        return isValid;
    }
}
