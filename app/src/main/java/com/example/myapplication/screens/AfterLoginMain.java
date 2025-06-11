package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.utils.SharedPreferencesUtil;
import com.google.android.material.navigation.NavigationView;

// מסך הבית הראשי אחרי התחברות - מציג את התפריט הראשי ומאפשר ניווט לכל חלקי האפליקציה
public class AfterLoginMain extends AppCompatActivity implements 
    View.OnClickListener, 
    NavigationView.OnNavigationItemSelectedListener {

    private Button btnAbout, btntest, btninfo, btnUpdate, btnLogout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // פונקציה שמופעלת כשהמסך נפתח בפעם הראשונה - מכינה את התפריט הצדדי ואת כל הכפתורים
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_after_login_main);
        
        // Initialize views
        initViews();
        
        // Setup toolbar
        setSupportActionBar(toolbar);
        
        // Setup navigation drawer
        setupNavigationDrawer();
        
        // Update navigation header with user info
        updateNavigationHeader();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // פונקציה שמאתחלת את כל הכפתורים והרכיבים במסך
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        
        btntest = findViewById(R.id.btnGotest);
        btninfo = findViewById(R.id.btninfo);
        btnAbout = findViewById(R.id.btnGoAbout);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);

        btntest.setOnClickListener(this);
        btninfo.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    // פונקציה שמגדירה את התפריט הצדדי ואת כפתור הפתיחה שלו
    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
        
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        navigationView.setNavigationItemSelectedListener(this);
    }

    // פונקציה שמעדכנת את פרטי המשתמש בתפריט הצדדי
    private void updateNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.nav_header_name);
        TextView tvEmail = headerView.findViewById(R.id.nav_header_email);
        
        User currentUser = SharedPreferencesUtil.getUser(this);
        if (currentUser != null) {
            String displayName = currentUser.getFname() + " " + currentUser.getLname();
            tvName.setText(displayName);
            tvEmail.setText(currentUser.getEmail());
        }
    }

    // פונקציה שמטפלת בלחיצות על הכפתורים הראשיים במסך
    @Override
    public void onClick(View v) {
        if (v == btntest) {
            startActivity(new Intent(getApplicationContext(), AddFood.class));
        } else if (v == btnAbout) {
            startActivity(new Intent(getApplicationContext(), DaysListActivity.class));
        } else if (v == btninfo) {
            startActivity(new Intent(getApplicationContext(), infouser.class));
        } else if (v == btnUpdate) {
            startActivity(new Intent(getApplicationContext(), UpdateUser.class));
        } else if (v == btnLogout) {
            logout();
        }
    }

    // פונקציה שמטפלת בבחירת פריט מהתפריט הצדדי
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_home) {
            // Already on home screen
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, infouser.class));
        } else if (id == R.id.nav_add_food) {
            startActivity(new Intent(this, AddFood.class));
        } else if (id == R.id.nav_logout) {
            logout();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // פונקציה שמטפלת בלחיצה על כפתור החזרה - סוגרת את התפריט אם הוא פתוח
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // פונקציה שמבצעת התנתקות מהמערכת וחזרה למסך הכניסה
    private void logout() {
        AuthenticationService.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
