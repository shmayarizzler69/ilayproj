package com.example.myapplication.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.SharedPreferencesUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

public class infouser extends AppCompatActivity {

    private ShapeableImageView ivUserAvatar;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infouser);

        // Initialize views
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvDisplayName = findViewById(R.id.tvDisplayName);
        TextView tvLastName = findViewById(R.id.tvLastName);
        TextView tvUserId = findViewById(R.id.tvUserId);
        TextView tvDailyCal = findViewById(R.id.tvDailyCal);
        MaterialButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnEdit = findViewById(R.id.btnEdit);
        FloatingActionButton fabChangePhoto = findViewById(R.id.fabChangePhoto);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        ivUserAvatar.setImageURI(selectedImageUri);
                        // TODO: Upload the image to your backend or save it locally
                        Toast.makeText(this, "תמונה נבחרה בהצלחה", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );

        // Retrieve the logged-in user
        User user = SharedPreferencesUtil.getUser(getApplicationContext());

        if (user != null) {
            // Display user details
            tvEmail.setText(user.getEmail());
            tvPhone.setText(user.getPhone() != null ? user.getPhone() : "N/A");
            tvDisplayName.setText(user.getFname() != null ? user.getFname() : "N/A");
            tvLastName.setText(user.getLname() != null ? user.getLname() : "N/A");
            tvUserId.setText("מזהה משתמש: " + user.getId());
            tvDailyCal.setText(user.getDailycal() != null ? String.valueOf(user.getDailycal()) : "N/A");
        } else {
            tvEmail.setText("משתמש לא מחובר");
        }

        // Set click listeners
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(infouser.this, AfterLoginMain.class);
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(infouser.this, UpdateUser.class);
            startActivity(intent);
        });

        fabChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
    }
}
