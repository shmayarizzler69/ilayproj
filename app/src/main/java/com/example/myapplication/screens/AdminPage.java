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

public class AdminPage extends AppCompatActivity implements View.OnClickListener {
    Button btnAbout,btntest,btninfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;



        });
        initViews();

    }
    private void initViews() {

        btntest= findViewById(R.id.btnGotest);
        btntest.setOnClickListener(this);
        btninfo=findViewById(R.id.btninfo);
        btninfo.setOnClickListener(this);
        btnAbout = findViewById(R.id.btnGoAbout);
        btnAbout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v==btntest){

            Intent goAdd=new Intent(getApplicationContext(), DeleteUserActivity.class);
            startActivity(goAdd);
        }
        if (v==btnAbout){
            Intent golog=new Intent(getApplicationContext(), DaysListActivity.class);
            startActivity(golog);
        }
        if (v==btninfo){

            Intent goinfo=new Intent(getApplicationContext(), UpdateUser.class);
            startActivity(goinfo);
        }
    }
}
