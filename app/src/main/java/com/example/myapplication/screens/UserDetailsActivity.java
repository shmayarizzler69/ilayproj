package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.services.DatabaseService.DatabaseCallback;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView userIdTextView;
    private EditText userFnameEditText, userLnameEditText, userPhoneEditText, userEmailEditText, userDailycalEditText, userheightedittext,userweightEditText,userageEditText,userGenderEditText;
    private Button saveChangesButton, deleteUserButton, backToDeleteUserActivityButton;

    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize views
        userIdTextView = findViewById(R.id.userIdTextView);
        userFnameEditText = findViewById(R.id.userFnameEditText);
        userLnameEditText = findViewById(R.id.userLnameEditText);
        userPhoneEditText = findViewById(R.id.userPhoneEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userDailycalEditText = findViewById(R.id.userDailycalEditText);
        //
        userheightedittext = findViewById(R.id.userheightedittext);
        userweightEditText= findViewById(R.id.userweightEditText);
        userageEditText = findViewById(R.id.userageEditText);
        userGenderEditText = findViewById(R.id.userGenderEditText);
        //
        saveChangesButton = findViewById(R.id.saveChangesButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);
        backToDeleteUserActivityButton = findViewById(R.id.backToDeleteUserActivityButton);

        // Initialize DatabaseService
        databaseService = DatabaseService.getInstance();

        // פונקציה שמציגה את פרטי המשתמש ומגדירה את פעולות הכפתורים
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            // Set user details in the TextViews and EditTexts
            userIdTextView.setText("ID: " + user.getId());
            userFnameEditText.setText(user.getFname());
            userLnameEditText.setText(user.getLname());
            userPhoneEditText.setText(user.getPhone());
            userEmailEditText.setText(user.getEmail());
            userDailycalEditText.setText(String.valueOf(user.getDailycal()));
            userheightedittext.setText(String.valueOf(user.getHeight()));
            userweightEditText.setText(String.valueOf(user.getWeight()));
            userageEditText.setText(user.getAge()+"");
            userGenderEditText.setText(String.valueOf(user.getAge()));
            // פונקציה שמעדכנת את פרטי המשתמש במסד הנתונים
            saveChangesButton.setOnClickListener(v -> {
                String updatedFname = userFnameEditText.getText().toString();
                String updatedLname = userLnameEditText.getText().toString();
                String updatedPhone = userPhoneEditText.getText().toString();
                String updatedEmail = userEmailEditText.getText().toString();
                String updatedDailycalString = userDailycalEditText.getText().toString();
                String updatedHeight= userheightedittext.getText().toString();
                String updatedWeight= userweightEditText.getText().toString();
                String updatedAge = userageEditText.getText().toString();
                String updatedGender =userGenderEditText.getText().toString();


                if (updatedFname.isEmpty() || updatedLname.isEmpty() || updatedPhone.isEmpty() || updatedEmail.isEmpty()||updatedHeight.isEmpty() || updatedWeight.isEmpty()|| updatedAge.isEmpty()|| updatedGender.isEmpty() || updatedDailycalString.isEmpty()) {
                    Toast.makeText(UserDetailsActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Integer updatedDailycal = Integer.parseInt(updatedDailycalString);

                    // Update the user object
                    user.setFname(updatedFname);
                    user.setLname(updatedLname);
                    user.setPhone(updatedPhone);
                    user.setEmail(updatedEmail);
                    user.setDailycal(updatedDailycal);
                    user.setHeight(Double.parseDouble(updatedHeight));
                    user.setWeight(Double.parseDouble(updatedWeight));
                    user.setAge(Integer.parseInt(updatedAge));
                    user.setGender(updatedGender);

                    // Update user data using DatabaseService
                    databaseService.updateUser(user, new DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void result) {
                            Toast.makeText(UserDetailsActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(UserDetailsActivity.this, "Failed to update user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(UserDetailsActivity.this, "Daily calorie count must be a valid number", Toast.LENGTH_SHORT).show();
                }
            });

            // פונקציה שמוחקת את המשתמש מהמערכת
            deleteUserButton.setOnClickListener(v -> {
                String userId = user.getId();
                databaseService.deleteUser(userId, new DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void result) {
                        Toast.makeText(UserDetailsActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful deletion
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(UserDetailsActivity.this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            // פונקציה שחוזרת למסך רשימת המשתמשים
            backToDeleteUserActivityButton.setOnClickListener(v -> {
                Intent backIntent = new Intent(UserDetailsActivity.this, DeleteUserActivity.class);
                startActivity(backIntent);
                finish(); // Close the current activity
            });

        } else {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user data is available
        }
    }
}
