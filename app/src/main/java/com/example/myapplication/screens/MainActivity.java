package com.example.myapplication.screens;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnRegister,btnLogin,btnAbout,btntest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    private void initViews() {
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btntest= findViewById(R.id.btntest);
        btntest.setOnClickListener(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v==btnRegister){

            Intent goReg=new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(goReg);
        }
        if (v==btntest){

            Intent goReg=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(goReg);
        }
        if (v==btnLogin){
            Intent golog=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(golog);
        }
    }
}