package com.example.myapplication.screens;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class Addfood extends AppCompatActivity {
    EditText etFood, etWhat;
    Button etCalc;
    String Food, What;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addfood);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init_views() {
        etWhat = findViewById(R.id.etWhat);
        etFood = findViewById(R.id.etFood);
        etCalc = findViewById(R.id.etCalc);

    }
    public void onClick(View v) {
        Food = etFood.getText().toString();
        What = etWhat.getText().toString();
    }




}