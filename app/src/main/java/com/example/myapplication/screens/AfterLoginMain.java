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
import com.example.myapplication.services.AuthenticationService;
import com.google.firebase.auth.UserInfo;

public class AfterLoginMain extends AppCompatActivity implements View.OnClickListener {
    Button btnAbout,btntest,btninfo,btnUpdate,btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_after_login_main);
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
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        btnLogout=findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v==btntest){

            Intent goAdd=new Intent(getApplicationContext(), Addfood.class);
            startActivity(goAdd);
        }
       if (v==btnAbout){
        Intent golog=new Intent(getApplicationContext(), DaysListActivity.class);
            startActivity(golog);
        }
        if (v==btninfo){

            Intent goinfo=new Intent(getApplicationContext(), infouser.class);
            startActivity(goinfo);
        }
        if (v==btnUpdate){
            Intent update=new Intent(getApplicationContext(), UpdateUser.class);
            startActivity(update);
        }
        if (v==btnLogout){

            AuthenticationService.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
